import { Router, Request, Response } from 'express';
import { z } from 'zod';
import { supabase } from '../index.js';

const router = Router();

router.get('/:id/time-slots', async (req: Request, res: Response) => {
  const { id } = req.params;
  const { date } = req.query;

  if (!date || typeof date !== 'string') {
    res.status(400).json({ error: 'date query param required (YYYY-MM-DD)' });
    return;
  }

  // 确保 date 格式正确
  if (!/^\d{4}-\d{2}-\d{2}$/.test(date)) {
    res.status(400).json({ error: 'date must be YYYY-MM-DD' });
    return;
  }

  // 检查 venue 是否存在
  const { data: venue, error: venueError } = await supabase
    .from('venues')
    .select('id')
    .eq('id', id)
    .eq('is_active', true)
    .single();

  if (venueError || !venue) {
    res.status(404).json({ error: 'Venue not found' });
    return;
  }

  // 先检查 DB 中是否有该天的时段
  const { data: existingSlots, error: fetchError } = await supabase
    .from('time_slots')
    .select('*')
    .eq('venue_id', id)
    .eq('date', date)
    .order('start_time');

  if (fetchError) {
    res.status(500).json({ error: 'Database error' });
    return;
  }

  // 如果 DB 中没有，则生成并插入
  if (!existingSlots || existingSlots.length === 0) {
    const slots = [];
    for (let hour = 8; hour < 22; hour++) {
      const startTime = `${hour.toString().padStart(2, '0')}:00:00`;
      const endTime = `${(hour + 1).toString().padStart(2, '0')}:00:00`;
      slots.push({ venue_id: id, date, start_time: startTime, end_time: endTime, is_available: true });
    }
    await supabase.from('time_slots').insert(slots);
    // 重新获取
    const { data: newSlots } = await supabase
      .from('time_slots')
      .select('*')
      .eq('venue_id', id)
      .eq('date', date)
      .order('start_time');
    res.json(newSlots || []);
    return;
  }

  res.json(existingSlots);
});

export default router;
