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
        assertEquals("add-product", WarrioDestination.AddProduct.route)
        assertEquals("receipt-scan", WarrioDestination.ReceiptScan.route)
        assertEquals("product-details", WarrioDestination.ProductDetails.route)
        assertEquals("add-document", WarrioDestination.AddDocument.route)
        assertNotEquals(WarrioDestination.Login.route, WarrioDestination.Dashboard.route)
        assertNotEquals(WarrioDestination.Login.route, WarrioDestination.Register.route)
        assertNotEquals(WarrioDestination.Register.route, WarrioDestination.Dashboard.route)
        assertNotEquals(WarrioDestination.Dashboard.route, WarrioDestination.Wallet.route)
        assertNotEquals(WarrioDestination.Wallet.route, WarrioDestination.AddProduct.route)
        assertNotEquals(WarrioDestination.Wallet.route, WarrioDestination.ReceiptScan.route)
        assertNotEquals(WarrioDestination.AddProduct.route, WarrioDestination.ReceiptScan.route)
        assertNotEquals(WarrioDestination.ReceiptScan.route, WarrioDestination.ProductDetails.route)
        assertNotEquals(
            WarrioDestination.AddProduct.route,
            WarrioDestination.ProductDetails.route,
        )
        assertNotEquals(
            WarrioDestination.ProductDetails.route,
            WarrioDestination.AddDocument.route,
        )
    }
}
