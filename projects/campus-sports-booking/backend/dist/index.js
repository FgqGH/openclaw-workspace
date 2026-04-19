"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.supabase = void 0;
require("dotenv/config");
const express_1 = __importDefault(require("express"));
const cors_1 = __importDefault(require("cors"));
const supabase_js_1 = require("@supabase/supabase-js");
const auth_js_1 = __importDefault(require("./routes/auth.js"));
const venues_js_1 = __importDefault(require("./routes/venues.js"));
const timeSlots_js_1 = __importDefault(require("./routes/timeSlots.js"));
const bookings_js_1 = __importDefault(require("./routes/bookings.js"));
exports.supabase = (0, supabase_js_1.createClient)(process.env.SUPABASE_URL, process.env.SUPABASE_SERVICE_KEY, { auth: { persistSession: false } });
const app = (0, express_1.default)();
app.use((0, cors_1.default)());
app.use(express_1.default.json());
app.use('/api/auth', auth_js_1.default);
app.use('/api/venues', venues_js_1.default);
app.use('/api/venues', timeSlots_js_1.default);
app.use('/api/bookings', bookings_js_1.default);
app.get('/health', (req, res) => {
    res.json({ status: 'ok' });
});
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
