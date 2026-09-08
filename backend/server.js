import express from "express";
import cookieParser from "cookie-parser";
import cors from "cors";
import crypto from "crypto";
import dotenv from "dotenv";

dotenv.config();

const app = express();
app.use(express.json());
app.use(cookieParser());
app.use(cors({ origin: true, credentials: true }));

const PORT = process.env.PORT || 3000;
const CLIENT_KEY = process.env.TIKTOK_CLIENT_KEY;
const CLIENT_SECRET = process.env.TIKTOK_CLIENT_SECRET;
const REDIRECT_URI = process.env.TIKTOK_REDIRECT_URI;

function requireConfig(res) {
  const missing = [];
  if (!CLIENT_KEY || CLIENT_KEY.startsWith("YOUR_")) missing.push("TIKTOK_CLIENT_KEY");
  if (!CLIENT_SECRET || CLIENT_SECRET.startsWith("YOUR_")) missing.push("TIKTOK_CLIENT_SECRET");
  if (!REDIRECT_URI || REDIRECT_URI.includes("YOUR_DOMAIN")) missing.push("TIKTOK_REDIRECT_URI");
  if (missing.length) {
    res.status(500).json({
      error: "Backend TikTok OAuth belum dikonfigurasi",
      missing
    });
    return true;
  }
  return false;
}

app.get("/health", (_, res) => {
  res.json({ ok: true, service: "TikTok AI Assistant Backend" });
});

app.get("/oauth/tiktok/start", (req, res) => {
  if (requireConfig(res)) return;

  const state = crypto.randomBytes(24).toString("hex");
  res.cookie("tiktok_oauth_state", state, {
    httpOnly: true,
    secure: true,
    sameSite: "lax",
    maxAge: 10 * 60 * 1000
  });

  const params = new URLSearchParams({
    client_key: CLIENT_KEY,
    scope: "user.info.basic",
    response_type: "code",
    redirect_uri: REDIRECT_URI,
    state
  });

  res.redirect(`https://www.tiktok.com/v2/auth/authorize/?${params.toString()}`);
});

app.get("/oauth/tiktok/callback", async (req, res) => {
  const { code, state, error, error_description } = req.query;

  if (error) {
    return res.status(400).json({ error, error_description });
  }

  if (!code || !state || state !== req.cookies.tiktok_oauth_state) {
    return res.status(400).json({
      error: "OAuth state/code tidak valid"
    });
  }

  try {
    const body = new URLSearchParams({
      client_key: CLIENT_KEY,
      client_secret: CLIENT_SECRET,
      code: String(code),
      grant_type: "authorization_code",
      redirect_uri: REDIRECT_URI
    });

    const tokenResponse = await fetch(
      "https://open.tiktokapis.com/v2/oauth/token/",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded"
        },
        body
      }
    );

    const tokenData = await tokenResponse.json();

    if (!tokenResponse.ok) {
      return res.status(tokenResponse.status).json({
        error: "Token exchange gagal",
        details: tokenData
      });
    }

    // Production: simpan token terenkripsi di database server.
    // Jangan pernah mengirim client_secret ke aplikasi Android.
    res.clearCookie("tiktok_oauth_state");
    res.type("html").send(`
      <html>
        <body style="font-family:sans-serif;background:#111;color:#fff;padding:30px">
          <h2>TikTok berhasil diotorisasi</h2>
          <p>Koneksi OAuth berhasil. Token harus disimpan aman di backend/database.</p>
          <p>Kamu bisa kembali ke aplikasi.</p>
        </body>
      </html>
    `);

  } catch (e) {
    res.status(500).json({
      error: "Backend OAuth error",
      message: e.message
    });
  }
});

app.listen(PORT, () => {
  console.log(`Backend berjalan di http://localhost:${PORT}`);
});
