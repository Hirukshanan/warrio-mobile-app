package com.warrio.app.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class WarrioDestinationTest {
    @Test
    fun routesAreStableAndUnique() {
        assertEquals("login", WarrioDestination.Login.route)
        assertEquals("dashboard", WarrioDestination.Dashboard.route)
        assertNotEquals(WarrioDestination.Login.route, WarrioDestination.Dashboard.route)
    }
}
