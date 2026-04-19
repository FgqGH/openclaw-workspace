import { Router, Response } from 'express';
import { z } from 'zod';
import { supabase } from '../index.js';
import { authMiddleware, AuthRequest } from '../middleware/auth.js';

const router = Router();

const CreateBookingSchema = z.object({
  venueId: z.string().uuid(),
  timeSlotId: z.string().uuid(),
  userId: z.string().uuid(),
});

function generateBookingCode(): string {
  return `BK${Date.now().toString(36).toUpperCase()}${Math.random().toString(36).slice(2, 5).toUpperCase()}`;
}

router.post('/', authMiddleware, async (req: AuthRequest, res: Response) => {
  const parsed = CreateBookingSchema.safeParse(req.body);
  if (!parsed.success) {
    res.status(400).json({ error: 'Invalid request', details: parsed.error.issues });
    return;
  }
  const { venueId, timeSlotId, userId } = parsed.data;

  // 验证当前登录用户是本人或管理员
  if (req.user!.userId !== userId && req.user!.role !== 'admin') {
    res.status(403).json({ error: 'Cannot book for other users' });
    return;
  }

  // 检查时段是否可用
  const { data: slot, error: slotError } = await supabase
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
  await supabase
    .from('time_slots')
    .update({ is_available: false })
    .eq('id', timeSlotId);

  // 创建预约
  const { data: booking, error: bookingError } = await supabase
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
    await supabase.from('time_slots').update({ is_available: true }).eq('id', timeSlotId);
    res.status(500).json({ error: 'Failed to create booking' });
    return;
  }

  res.status(201).json(booking);
});

router.get('/my', authMiddleware, async (req: AuthRequest, res: Response) => {
  const { data, error } = await supabase
    .from('bookings')
    .select(`
      *,
      venues(name, type, location),
      time_slots(date, start_time, end_time)
    `)
    .eq('user_id', req.user!.userId)
    .order('created_at', { ascending: false });

  if (error) {
    res.status(500).json({ error: 'Database error' });
    return;
  }
  res.json(data);
});

router.delete('/:id', authMiddleware, async (req: AuthRequest, res: Response) => {
  const { id } = req.params;

  const { data: booking, error: fetchError } = await supabase
    .from('bookings')
    .select('*')
    .eq('id', id)
    .single();

  if (fetchError || !booking) {
    res.status(404).json({ error: 'Booking not found' });
    return;
  }

  if (booking.user_id !== req.user!.userId && req.user!.role !== 'admin') {
    res.status(403).json({ error: 'Cannot cancel others bookings' });
    return;
  }

  if (booking.status === 'cancelled') {
    res.status(400).json({ error: 'Already cancelled' });
    return;
  }

  // 检查是否提前1小时
  const { data: slot } = await supabase
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
  await supabase.from('bookings').update({ status: 'cancelled' }).eq('id', id);
  await supabase.from('time_slots').update({ is_available: true }).eq('id', booking.time_slot_id);

  res.json({ success: true });
});

export default router;
