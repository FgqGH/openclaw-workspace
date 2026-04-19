import { Router, Request, Response } from 'express';
import { z } from 'zod';
import jwt from 'jsonwebtoken';
import { supabase } from '../index.js';
import { authMiddleware, AuthRequest } from '../middleware/auth.js';

const router = Router();

const LoginSchema = z.object({
  phone: z.string().min(1),
  name: z.string().optional(),
});

router.post('/login', async (req: Request, res: Response) => {
  const parsed = LoginSchema.safeParse(req.body);
  if (!parsed.success) {
    res.status(400).json({ error: 'Invalid request', details: parsed.error.issues });
    return;
  }
  const { phone, name } = parsed.data;

  // 查找用户
  let { data: profiles, error } = await supabase
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
    const { data: newProfile, error: createError } = await supabase
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

  const token = jwt.sign(
    { userId: profile.id, phone: profile.phone, role: profile.role },
    process.env.JWT_SECRET!,
    { expiresIn: '7d' }
  );

  res.json({ token, user: profile });
});

router.get('/me', authMiddleware, async (req: AuthRequest, res: Response) => {
  const { data: profile, error } = await supabase
    .from('profiles')
    .select('*')
    .eq('id', req.user!.userId)
    .single();

  if (error || !profile) {
    res.status(404).json({ error: 'User not found' });
    return;
  }
  res.json(profile);
});

export default router;
