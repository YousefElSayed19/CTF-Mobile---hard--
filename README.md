# NexaWallet — Mobile CTF Challenge (Hard)

تطبيق أندرويد مصمم كـ CTF challenge يعتمد بالكامل على **dynamic instrumentation**
(Frida) بدل الـ static reverse engineering. السيناريو: محفظة رقمية وهمية
فيها حد أقصى للتحويل، والهدف إن اللاعب يتخطاه عن طريق التلاعب في سلوك
التطبيق وقت التشغيل.

> ⚠️ ده مشروع تعليمي لأغراض CTF/تدريب أمني فقط. الأسماء والعلامة التجارية
> (NexaWallet) وهمية بالكامل ومالهاش علاقة بأي تطبيق أو بنك حقيقي.

---

## حالة المشروع الحالية

- **لا يوجد Backend حقيقي بعد.** كل البيانات (اليوزرات، الرصيد، الحد
  الأقصى) متخزنة محليًا في `SharedPreferences` عن طريق `MockBackend.kt`،
  لحد ما يتم بناء سيرفر حقيقي وربطه لاحقًا.
- الـ SSL Pinning (في `PinningConfig.kt` و `WebViewTrustManager.kt`) مبني
  بالكامل لكن بيستخدم قيم placeholder (pin وهمي) لأنه مفيش سيرفر حقيقي
  يتصل بيه دلوقتي. هيحتاج تحديث لما يتضاف السيرفر.
- توليد الـ "flag" في `TransferActivity.generateBypassToken()` محلي
  مؤقتًا. في النسخة النهائية، الأفضل إن السيرفر هو اللي يتحقق من إن
  التحويل فعلاً حصل ويرجع الـ flag الحقيقي، مش التطبيق نفسه.

---

## هيكل المشروع

```
app/src/main/java/com/example/nexawallet/
├── ui/
│   ├── LoginActivity.kt        # تسجيل الدخول
│   ├── RegisterActivity.kt     # إنشاء حساب جديد (register بسيط)
│   ├── HomeActivity.kt         # عرض الرصيد + الحد الأقصى
│   ├── TransferActivity.kt     # ★ شاشة التحدي الأساسية (الثغرة هنا)
│   ├── WebTransferActivity.kt  # شاشة WebView (تستضيف SSL pinning التاني)
│   └── BlockedActivity.kt      # شاشة الحظر لو حصل كشف تلاعب
├── security/
│   ├── RootFridaDetector.kt    # نقطة كشف root/Frida الأولى (فحص ملفات + بورت)
│   └── MapsIntegrityCheck.kt   # نقطة كشف ثانية مستقلة (فحص /proc/self/maps)
├── network/
│   ├── PinningConfig.kt        # SSL Pinning - طبقة OkHttp العادية
│   └── WebViewTrustManager.kt  # SSL Pinning - طبقة WebView المنفصلة
└── util/
    ├── MockBackend.kt          # محاكاة بيانات سيرفر محليًا (مؤقت)
    └── SessionManager.kt       # حالة الجلسة في الذاكرة (sessionApproved)
```

---

## رحلة اللاعب المتوقعة (بدون حل الحل بالتفصيل هنا)

1. يفتح التطبيق، يعمل حساب (Register)، يسجل دخول.
2. يشوف في الشاشة الرئيسية الحد الأقصى للتحويل.
3. يروح شاشة Transfer، يحاول يحوّل مبلغ أكبر من الحد → بيترفض.
4. يحاول يفهم منطق الرفض عن طريق أدوات dynamic analysis (Frida tracing)
   بدل قراءة الكود الثابت بس.
5. يكتشف إن في نقطتين فحص مستقلتين لـ root/Frida (`RootFridaDetector`
   و `MapsIntegrityCheck`) لازم الاتنين يتعدّوا مع بعض.
6. يكتشف إن القرار الفعلي بيتاخد في `validateOp()` جوه `TransferActivity`،
   وإنها بتعتمد على قيمتين: الحد الأقصى، وحالة الجلسة.
7. يعمل hook مناسب يخلي العملية تعدي رغم تجاوز الحد.
8. ياخد الـ token/flag الظاهر على الشاشة.

---

## خطوات البناء (Build)

المشروع محتاج **Android Studio** (مش هيتبني من غير Android SDK):

1. افتح المشروع في Android Studio (File → Open → اختار فولدر `NexaWallet`).
2. سيب Android Studio يعمل Gradle Sync تلقائيًا (هيطلب تحميل الـ Gradle
   wrapper ومكتبات OkHttp/AndroidX أول مرة — محتاج اتصال إنترنت).
3. شغّل التطبيق على **إيموليتور Rooted** (أو جهاز حقيقي معمول له root)
   عشان تقدر تستخدم Frida عليه.

### ملاحظة مهمة قبل التسليم النهائي

- لازم تستبدل قيم الـ placeholder في `PinningConfig.kt` و
  `WebViewTrustManager.kt` بقيم حقيقية لما يتم ربط سيرفر فعلي.
- اسم الـ package (`com.example.nexawallet`) الأفضل تغيّره لاسم خاص
  بيك/بالمسابقة قبل التوزيع النهائي.
- فكّر في تفعيل ProGuard/R8 (minifyEnabled) في الـ release build لو
  عايز تزود صعوبة أي جزء static analysis يحصل بالصدفة.

---

## خطوات لسه ناقصة (مش جزء من هذا التسليم)

- [ ] بناء Backend حقيقي (endpoints: register/login/balance/transfer/verify-flag)
- [ ] ربط قيم SSL pinning الحقيقية بشهادة السيرفر الفعلي
- [ ] اختبار داخلي كامل للتحدي (حله بنفسك من الصفر بـ Frida) للتأكد من
      مستوى الصعوبة الفعلي
- [ ] كتابة write-up/حل رسمي للمنظمين
