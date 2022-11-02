package com.realityexpander.tasky.agenda_feature.presentation.common.util

import junit.framework.TestCase.assertTrue
import org.junit.Test

class GetUserAcronymKtTest {


    @Test
    fun `getUserAcronym() for First, Last name`() {
        // ARRANGE / ACT / ASSERT
        assertTrue(getUserAcronym("John Doe") == "JD")
    }

    @Test
    fun `getUserAcronym() for First name`() {
        // ARRANGE / ACT / ASSERT
        assertTrue(getUserAcronym("John") == "JO")
    }

    @Test
    fun `getUserAcronym() for single letter`() {
        // ARRANGE / ACT / ASSERT
        assertTrue(getUserAcronym("J") == "J")
    }

    @Test
    fun `getUserAcronym() for empty string`() {
        // ARRANGE / ACT / ASSERT
        assertTrue(getUserAcronym("") == "??")
    }

    @Test
    fun `getUserAcronym() for First, Last, Middle name`() {
        // ARRANGE / ACT / ASSERT
        assertTrue(getUserAcronym("John Doe Smith") == "JS")
    }

    @Test
    fun `getUserAcronym() for First, Last, Middle name with Jr`() {
        // ARRANGE / ACT / ASSERT
        assertTrue(getUserAcronym("John Doe Smith Jr.") == "JJ")
    }

    @Test
    fun `getUserAcronym() for First, Last, Middle name with Jr and III`() {
        // ARRANGE / ACT / ASSERT
        assertTrue(getUserAcronym("John Doe Smith Jr. III") == "JI")
    }

    @Test
    fun `getUserAcronym() for First, Last, Middle name with Jr, III, IV`() {
        // ARRANGE / ACT / ASSERT
        assertTrue(getUserAcronym("John Doe Smith Jr. III IV") == "JI")
    }

    @Test
    fun `getUserAcronym() for First, Last, Middle name with Jr, III, IV, V`() {
        // ARRANGE / ACT / ASSERT
        assertTrue(getUserAcronym("John Doe Smith Jr. III IV V") == "JV")
    }
}