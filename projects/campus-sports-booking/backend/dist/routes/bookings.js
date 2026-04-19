"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const zod_1 = require("zod");
const index_js_1 = require("../index.js");
const auth_js_1 = require("../middleware/auth.js");
const router = (0, express_1.Router)();
const CreateBookingSchema = zod_1.z.object({
    venueId: zod_1.z.string().uuid(),
    timeSlotId: zod_1.z.string().uuid(),
    userId: zod_1.z.string().uuid(),
});
function generateBookingCode() {
    return `BK${Date.now().toString(36).toUpperCase()}${Math.random().toString(36).slice(2, 5).toUpperCase()}`;
}
router.post('/', auth_js_1.authMiddleware, async (req, res) => {
    const parsed = CreateBookingSchema.safeParse(req.body);
    if (!parsed.success) {
        res.status(400).json({ error: 'Invalid request', details: parsed.error.issues });
        return;
    }
    const { venueId, timeSlotId, userId } = parsed.data;
    // 验证当前登录用户是本人或管理员
    if (req.user.userId !== userId && req.user.role !== 'admin') {
        res.status(403).json({ error: 'Cannot book for other users' });
        return;
    }
    // 检查时段是否可用
    const { data: slot, error: slotError } = await index_js_1.supabase
        .from('time_slots')
        .select('*')
        .eq('id', timeSlotId)
        .eq('venue_id', venueId)
        .single();
    if (slotError || !slot) {
        res.status(404).json({ error: 'Time slot not found' });
        return;
    }
    if (!slot.is_available) {
        res.status(409).json({ error: 'Time slot is not available' });
        return;
    }
    // 标记为不可用
    await index_js_1.supabase
        .from('time_slots')
        .update({ is_available: false })
        .eq('id', timeSlotId);
    // 创建预约
    const { data: booking, error: bookingError } = await index_js_1.supabase
        .from('bookings')
        .insert({
        user_id: userId,
        venue_id: venueId,
        time_slot_id: timeSlotId,
        booking_code: generateBookingCode(),
        status: 'confirmed',
    })
        .select()
        .single();
    if (bookingError) {
        // 回滚
        await index_js_1.supabase.from('time_slots').update({ is_available: true }).eq('id', timeSlotId);
        res.status(500).json({ error: 'Failed to create booking' });
        return;
    }
    res.status(201).json(booking);
});
router.get('/my', auth_js_1.authMiddleware, async (req, res) => {
    const { data, error } = await index_js_1.supabase
        .from('bookings')
        .select(`
      *,
      venues(name, type, location),
      time_slots(date, start_time, end_time)
    `)
        .eq('user_id', req.user.userId)
        .order('created_at', { ascending: false });
    if (error) {
        res.status(500).json({ error: 'Database error' });
        return;
    }
    res.json(data);
});
router.delete('/:id', auth_js_1.authMiddleware, async (req, res) => {
    const { id } = req.params;
    const { data: booking, error: fetchError } = await index_js_1.supabase
        .from('bookings')
        .select('*')
        .eq('id', id)
        .single();
    if (fetchError || !booking) {
        res.status(404).json({ error: 'Booking not found' });
        return;
    }
    if (booking.user_id !== req.user.userId && req.user.role !== 'admin') {
        res.status(403).json({ error: 'Cannot cancel others bookings' });
        return;
    }
    if (booking.status === 'cancelled') {
        res.status(400).json({ error: 'Already cancelled' });
        return;
    }
    // 检查是否提前1小时
    const { data: slot } = await index_js_1.supabase
        .from('time_slots')
        .select('date, start_time')
        .eq('id', booking.time_slot_id)
        .single();
    if (slot) {
        const slotDateTime = new Date(`${slot.date}T${slot.start_time}`);
        const now = new Date();
        const diffMs = slotDateTime.getTime() - now.getTime();
        const diffHours = diffMs / (1000 * 60 * 60);
        if (diffHours < 1) {
            res.status(400).json({ error: 'Cannot cancel within 1 hour of the slot' });
            return;
        }
    }
    // 取消预约并恢复时段
    await index_js_1.supabase.from('bookings').update({ status: 'cancelled' }).eq('id', id);
    await index_js_1.supabase.from('time_slots').update({ is_available: true }).eq('id', booking.time_slot_id);
    res.json({ success: true });
});
exports.default = router;
