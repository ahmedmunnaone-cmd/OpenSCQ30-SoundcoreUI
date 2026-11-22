# Soundcore-style UI দিয়ে OpenSCQ30 APK বানানোর নিয়ম

1. GitHub-এ একটি নতুন repository বানান (Private হলেও চলবে)।
2. এই ZIP-এর ভেতরের সব ফাইল (`OpenSCQ30-2.13.0` ফোল্ডারের ভেতরের জিনিস) repository-র root-এ আপলোড/push করুন।
   - `.github` ফোল্ডারটা অবশ্যই থাকতে হবে।
3. GitHub-এ **Actions** ট্যাবে যান → **Build installable APK** → **Run workflow**।
   - বেশিরভাগ ফোনের জন্য `arm64-v8a` সিলেক্ট রাখুন।
4. ২০–৪০ মিনিট পর বিল্ড শেষ হলে ওই run-এর নিচে **Artifacts** থেকে APK ডাউনলোড করুন (zip-এর ভেতরে `.apk`)।
5. ফোনে APK ইন্সটল করুন (Unknown sources allow করতে হবে)। অ্যাপের নাম/প্যাকেজ: `com.oppzippy.openscq30.debug`।

## কী কী বদলানো হয়েছে
- নীল গ্লাস থিম + গ্রেডিয়েন্ট ব্যাকগ্রাউন্ড (Soundcore স্টাইল)
- নিচে ৩টি ট্যাব: Home / Sound / All Settings
- Home: ডিভাইসের নাম, ইয়ারবাডসের ছবি, L/Case/R ব্যাটারি, ANC / Normal / Transparency বাটন
- Sound: Equalizer প্রিসেট চিপ, ভার্টিকাল ব্যান্ড স্লাইডার, সারাউন্ড টগল, Custom EQ
- All Settings: সব on/off সেটিং স্যুইচসহ, বাকি ক্যাটাগরি তীর চিহ্নসহ

## সমস্যা হলে
Actions-এর লাল ফেইল হওয়া step-এর error লগ কপি করে আমাকে দিন, ঠিক করে দেব।
