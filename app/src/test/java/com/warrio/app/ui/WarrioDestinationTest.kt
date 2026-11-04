package com.warrio.app.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class WarrioDestinationTest {
    @Test
    fun routesAreStableAndUnique() {
        assertEquals("login", WarrioDestination.Login.route)
        assertEquals("register", WarrioDestination.Register.route)
        assertEquals("dashboard", WarrioDestination.Dashboard.route)
        assertEquals("wallet", WarrioDestination.Wallet.route)
        assertNotEquals(WarrioDestination.Login.route, WarrioDestination.Dashboard.route)
        assertNotEquals(WarrioDestination.Login.route, WarrioDestination.Register.route)
        assertNotEquals(WarrioDestination.Register.route, WarrioDestination.Dashboard.route)
        assertNotEquals(WarrioDestination.Dashboard.route, WarrioDestination.Wallet.route)
    }
}
