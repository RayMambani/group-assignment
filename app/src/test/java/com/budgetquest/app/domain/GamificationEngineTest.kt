package com.budgetquest.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class GamificationEngineTest {

    @Test
    fun `calculateLevel returns correct level for XP`() {
        assertEquals(1, GamificationEngine.calculateLevel(0))
        assertEquals(1, GamificationEngine.calculateLevel(499))
        assertEquals(2, GamificationEngine.calculateLevel(500))
        assertEquals(3, GamificationEngine.calculateLevel(1000))
    }

    @Test
    fun `getXPForNextLevel returns correct XP threshold`() {
        assertEquals(500, GamificationEngine.getXPForNextLevel(1))
        assertEquals(1000, GamificationEngine.getXPForNextLevel(2))
        assertEquals(5000, GamificationEngine.getXPForNextLevel(10))
    }

    @Test
    fun `getLevelName returns correct rank based on level`() {
        assertEquals("Budget Novice", GamificationEngine.getLevelName(1))
        assertEquals("Budget Novice", GamificationEngine.getLevelName(4))
        assertEquals("Frugal Squire", GamificationEngine.getLevelName(5))
        assertEquals("Frugal Squire", GamificationEngine.getLevelName(9))
        assertEquals("Finance Knight", GamificationEngine.getLevelName(10))
        assertEquals("Finance Knight", GamificationEngine.getLevelName(19))
        assertEquals("Financial Expert", GamificationEngine.getLevelName(20))
        assertEquals("Financial Expert", GamificationEngine.getLevelName(100))
    }
}
