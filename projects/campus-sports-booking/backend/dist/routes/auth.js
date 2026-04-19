"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const zod_1 = require("zod");
const jsonwebtoken_1 = __importDefault(require("jsonwebtoken"));
const index_js_1 = require("../index.js");
const auth_js_1 = require("../middleware/auth.js");
const router = (0, express_1.Router)();
const LoginSchema = zod_1.z.object({
    phone: zod_1.z.string().min(1),
    name: zod_1.z.string().optional(),
});
router.post('/login', async (req, res) => {
    const parsed = LoginSchema.safeParse(req.body);
    if (!parsed.success) {
        res.status(400).json({ error: 'Invalid request', details: parsed.error.issues });
        return;
    }
    const { phone, name } = parsed.data;
    // 查找用户
    let { data: profiles, error } = await index_js_1.supabase
        .from('profiles')
        .select('*')
        .eq('phone', phone)
        .limit(1);
    if (error) {
        res.status(500).json({ error: 'Database error' });
        return;
    }
    let profile = profiles?.[0];
    if (!profile) {
        // 不存在则创建
        const { data: newProfile, error: createError } = await index_js_1.supabase
            .from('profiles')
            .insert({ phone, name: name || '', role: 'user', user_number: `U${Date.now()}` })
            .select()
            .single();
        if (createError) {
            res.status(500).json({ error: 'Failed to create user' });
            return;
        }
        profile = newProfile;
    }
    const token = jsonwebtoken_1.default.sign({ userId: profile.id, phone: profile.phone, role: profile.role }, process.env.JWT_SECRET, { expiresIn: '7d' });
    res.json({ token, user: profile });
});
router.get('/me', auth_js_1.authMiddleware, async (req, res) => {
    const { data: profile, error } = await index_js_1.supabase
        .from('profiles')
        .select('*')
        .eq('id', req.user.userId)
        .single();
    if (error || !profile) {
        res.status(404).json({ error: 'User not found' });
        return;
    }
    res.json(profile);
});
exports.default = router;
