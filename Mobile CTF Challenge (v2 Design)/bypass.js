Java.perform(function () {

    // Gate 1 — frida port probe
    var Socket = Java.use("java.net.Socket");
    Socket.connect.overload("java.net.SocketAddress", "int").implementation = function (addr, timeout) {
        if (addr.toString().indexOf("27042") !== -1) {
            throw Java.use("java.io.IOException").$new("blocked by hook");
        }
        return this.connect(addr, timeout);
    };

    // Gate 1 — /proc/self/maps scan
    var BufferedReader = Java.use("java.io.BufferedReader");
    BufferedReader.readLine.overload().implementation = function () {
        var line = this.readLine();
        while (line !== null) {
            var lower = line.toLowerCase();
            if (lower.indexOf("frida") !== -1 || lower.indexOf("linjector") !== -1 || lower.indexOf("xposed") !== -1) {
                line = this.readLine();
                continue;
            }
            break;
        }
        return line;
    };

    // Gate 1 — suspicious Frida thread names
    var Thread = Java.use("java.lang.Thread");
    Thread.getName.implementation = function () {
        var name = this.getName();
        if (name !== null) {
            var lower = name.toLowerCase();
            if (["gum-js-loop", "gmain", "gdbus", "frida"].some(k => lower.indexOf(k) !== -1)) {
                return "RenamedThread";
            }
        }
        return name;
    };

    console.log("[+] Gate 1 hooked at the framework level.");
});