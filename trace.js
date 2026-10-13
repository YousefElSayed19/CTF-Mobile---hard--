Java.perform(function () {
    console.log('[*] Tracing all activity transitions...');

    var Activity = Java.use('android.app.Activity');
    Activity.startActivity.overload('android.content.Intent').implementation = function (intent) {
        try {
            var comp = intent.getComponent();
            if (comp != null) {
                console.log('[TRACE] startActivity -> ' + comp.getClassName());
            }
        } catch (e) {}
        this.startActivity(intent);
    };

    console.log('[*] Trace ready');
});