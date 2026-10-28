package com.aira.app

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class SendAccessibilityService : AccessibilityService() {

    companion object {
        @Volatile private var armedUntil = 0L
        fun arm() { armedUntil = System.currentTimeMillis() + 20000 }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (System.currentTimeMillis() > armedUntil) return
        val root = rootInActiveWindow ?: return
        var target: AccessibilityNodeInfo? = null
        for (id in listOf("com.whatsapp:id/send", "com.whatsapp.w4b:id/send")) {
            target = root.findAccessibilityNodeInfosByViewId(id).firstOrNull()
            if (target != null) break
        }
        if (target == null) target = findSend(root)
        var node = target
        while (node != null && !node.isClickable) node = node.parent
        if (node != null) {
            armedUntil = 0L
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }

    private fun findSend(n: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (n == null) return null
        if (n.contentDescription?.toString().equals("Send", ignoreCase = true)) return n
        for (i in 0 until n.childCount) {
            val r = findSend(n.getChild(i))
            if (r != null) return r
        }
        return null
    }

    override fun onInterrupt() {}
}
