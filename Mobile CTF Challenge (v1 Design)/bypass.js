Java.perform(function () {

    // 1) تعطيل فحص ملفات su
    var File = Java.use("java.io.File");
    File.exists.implementation = function () {
        var path = this.getAbsolutePath();
        if (path.indexOf("su") !== -1 || path.indexOf("Superuser") !== -1) {
            console.log("[File.exists bypass] " + path);
            return false;
        }
        return this.exists();
    };

    // 2) تعطيل فحص بورت فريدا (27042)
    var Socket = Java.use("java.net.Socket");
    Socket.connect.overload("java.net.SocketAddress", "int").implementation = function (addr, timeout) {
        var addrStr = addr.toString();
        if (addrStr.indexOf("27042") !== -1) {
            console.log("[Socket.connect bypass] blocked port check: " + addrStr);
            throw Java.use("java.io.IOException").$new("blocked by hook");
        }
        return this.connect(addr, timeout);
    };

    // 3) تعطيل فحص /proc/self/maps (فلترة سطور frida)
    var BufferedReader = Java.use("java.io.BufferedReader");
    BufferedReader.readLine.overload().implementation = function () {
        var line = this.readLine();
        while (line !== null && (line.toLowerCase().indexOf("frida") !== -1 ||
                                  line.toLowerCase().indexOf("linjector") !== -1 ||
                                  line.toLowerCase().indexOf("xposed") !== -1)) {
            console.log("[maps bypass] filtered line");
            line = this.readLine();
        }
        return line;
    };

    // 4) تعطيل فحص الـ limit في TransferActivity
    var TransferActivity = Java.use("com.example.nexawallet.ui.TransferActivity");

    // اطبع كل overloads الموجودة لـ validateOp عشان نتأكد من الاسم الصحيح
    console.log("[DEBUG] validateOp overloads: " + TransferActivity.validateOp.overloads.length);

    TransferActivity.validateOp.overloads.forEach(function (ov) {
        ov.implementation = function (amount) {
            console.log("[validateOp bypass] amount=" + amount + " -> forcing true");
            return true;
        };
    });

    // 5) تشخيص إضافي: تأكيد إن الزرار بيوصل للدالة أصلاً
    TransferActivity.handleTransferClick.implementation = function (username) {
        console.log("[DEBUG] handleTransferClick called, username=" + username);
        var result = this.handleTransferClick(username);
        console.log("[DEBUG] handleTransferClick finished");
        return result;
    };

    console.log("[+] Hooks installed: su check, frida port, maps check, validateOp (all overloads), handleTransferClick debug");
});