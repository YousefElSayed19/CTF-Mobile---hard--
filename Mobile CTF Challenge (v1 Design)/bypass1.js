Java.perform(function () {

    console.log('[*] Starting bypass...');

    try {
        var RootFridaDetector = Java.use('com.example.nexawallet.security.RootFridaDetector');
        RootFridaDetector.isDeviceCompromised.implementation = function () {
            console.log('[HOOK] RootFridaDetector.isDeviceCompromised -> false');
            return false;
        };
    } catch (err) {
        console.log('[-] RootFridaDetector hook failed: ' + err);
    }

    try {
        var MapsIntegrityCheck = Java.use('com.example.nexawallet.security.MapsIntegrityCheck');
        MapsIntegrityCheck.isMemoryTampered.implementation = function () {
            console.log('[HOOK] MapsIntegrityCheck.isMemoryTampered -> false');
            return false;
        };
    } catch (err) {
        console.log('[-] MapsIntegrityCheck hook failed: ' + err);
    }

    try {
        var TransferActivity = Java.use('com.example.nexawallet.ui.TransferActivity');
        TransferActivity.validateOp.implementation = function (amount) {
            console.log('[HOOK] validateOp(' + amount + ') -> true (forced)');
            return true;
        };
    } catch (err) {
        console.log('[-] TransferActivity hook failed: ' + err);
    }

    try {
        var Activity = Java.use('android.app.Activity');
        Activity.startActivity.overload('android.content.Intent').implementation = function (intent) {
            try {
                var comp = intent.getComponent();
                if (comp != null && comp.getClassName().indexOf('BlockedActivity') >= 0) {
                    console.log('[HOOK] Suppressed navigation to BlockedActivity');
                    return;
                }
            } catch (e) {}
            this.startActivity(intent);
        };
    } catch (err) {
        console.log('[-] startActivity hook failed: ' + err);
    }

    try {
        var TransferActivity2 = Java.use('com.example.nexawallet.ui.TransferActivity');
        TransferActivity2.finish.overload().implementation = function () {
            console.log('[HOOK] Suppressed finish() on TransferActivity');
        };
    } catch (err) {
        console.log('[-] finish() hook failed: ' + err);
    }

    console.log('[*] All hooks ready');
});