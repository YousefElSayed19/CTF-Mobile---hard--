Java.perform(function () {
    var ActivityThread = Java.use("android.app.ActivityThread");
    var app = ActivityThread.currentApplication();
    var Intent = Java.use("android.content.Intent");
    var intent = Intent.$new();
    intent.setClassName("com.example.nexawallet", "com.example.nexawallet.ui.ReviewGateActivity");
    intent.addFlags(0x10000000); // FLAG_ACTIVITY_NEW_TASK
    app.startActivity(intent);
});