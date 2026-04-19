import { Router, Request, Response } from 'express';
import { z } from 'zod';
import { supabase } from '../index.js';
import { authMiddleware, adminMiddleware, AuthRequest } from '../middleware/auth.js';

const router = Router();

router.get('/', async (req: Request, res: Response) => {
  const { data, error } = await supabase
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

const CreateVenueSchema = z.object({
  name: z.string().min(1),
  type: z.string().min(1),
  location: z.string().min(1),
  image_url: z.string().optional(),
});

router.post('/', authMiddleware, adminMiddleware, async (req: Request, res: Response) => {
  const parsed = CreateVenueSchema.safeParse(req.body);
  if (!parsed.success) {
    res.status(400).json({ error: 'Invalid request', details: parsed.error.issues });
    return;
  }

  const { data, error } = await supabase
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

router.delete('/:id', authMiddleware, adminMiddleware, async (req: Request, res: Response) => {
  const { error } = await supabase
    .from('venues')
    .update({ is_active: false })
    .eq('id', req.params.id);

  if (error) {
    res.status(500).json({ error: 'Failed to delete venue' });
    return;
  }
  res.json({ success: true });
});

export default router;
