"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const zod_1 = require("zod");
const index_js_1 = require("../index.js");
const auth_js_1 = require("../middleware/auth.js");
const router = (0, express_1.Router)();
router.get('/', async (req, res) => {
    const { data, error } = await index_js_1.supabase
        .from('venues')
        .select('*')
        .eq('is_active', true)
        .order('created_at');
    if (error) {
        res.status(500).json({ error: 'Database error' });
        return;
    }
    res.json(data);
});
const CreateVenueSchema = zod_1.z.object({
    name: zod_1.z.string().min(1),
    type: zod_1.z.string().min(1),
    location: zod_1.z.string().min(1),
    image_url: zod_1.z.string().optional(),
});
router.post('/', auth_js_1.authMiddleware, auth_js_1.adminMiddleware, async (req, res) => {
    const parsed = CreateVenueSchema.safeParse(req.body);
    if (!parsed.success) {
        res.status(400).json({ error: 'Invalid request', details: parsed.error.issues });
        return;
    }
    const { data, error } = await index_js_1.supabase
        .from('venues')
        .insert({ ...parsed.data, is_active: true })
        .select()
        .single();
    if (error) {
        res.status(500).json({ error: 'Failed to create venue' });
        return;
    }
    res.status(201).json(data);
});
router.delete('/:id', auth_js_1.authMiddleware, auth_js_1.adminMiddleware, async (req, res) => {
    const { error } = await index_js_1.supabase
        .from('venues')
        .update({ is_active: false })
        .eq('id', req.params.id);
    if (error) {
        res.status(500).json({ error: 'Failed to delete venue' });
        return;
    }
    res.json({ success: true });
});
exports.default = router;
