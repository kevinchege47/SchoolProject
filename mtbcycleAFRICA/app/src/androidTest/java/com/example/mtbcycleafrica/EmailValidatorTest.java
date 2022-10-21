package com.example.mtbcycleafrica;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class EmailValidatorTest {

    /**
     * Unit tests for the EmailValidator logic.
     */
    public class EmailValidatorTest {
        @Test
        public void emailValidator_CorrectEmailSimple_ReturnsTrue() {
            assertTrue(user_signup.validateEmail("name@email.com"));
        }
        @Test
        public void emailValidator_CorrectEmailSubDomain_ReturnsTrue() {
            assertTrue(user_signup.validateEmai("name@email.co.uk"));
        }
        @Test
        public void emailValidator_InvalidEmailNoTld_ReturnsFalse() {
            assertFalse(user_signup.validateEmai("name@email"));
        }
        @Test
        public void emailValidator_InvalidEmailDoubleDot_ReturnsFalse() {
            assertFalse(user_signup.validateEmai("name@email..com"));
        }
        @Test
        public void emailValidator_InvalidEmailNoUsername_ReturnsFalse() {
            assertFalse(user_signup.validateEmai("@email.com"));
        }
        @Test
        public void emailValidator_EmptyString_ReturnsFalse() {
            assertFalse(user_signup.validateEmai(""));
        }
        @Test
        public void emailValidator_NullEmail_ReturnsFalse() {
            assertFalse(user_signup.validateEmai(null));
        }
    }
}
