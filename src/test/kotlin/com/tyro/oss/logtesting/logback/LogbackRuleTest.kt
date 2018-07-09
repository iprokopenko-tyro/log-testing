/*
 * Copyright 2018 Tyro Payments Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tyro.oss.logtesting.logback

import ch.qos.logback.classic.Level
import com.tyro.oss.logtesting.logback.LogbackRuleAssert.Companion.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Rule
import org.junit.Test
import org.slf4j.LoggerFactory

class LogbackRuleTest {

    private val LOG = LoggerFactory.getLogger(LogbackRuleTest::class.java)

    @get:Rule
    var log = LogbackRule(LogbackRuleTest::class)

    @Test
    fun shouldClearLog() {
        LOG.info("test message")

        assertThat(log).isNotEmpty()

        log.clear()

        assertThat(log).isEmpty()
    }

    @Test
    fun shouldAssertLogSize() {
        assertThat(log).hasSize(0)

        LOG.info("test message 1")
        LOG.info("test message 2")
        LOG.info("test message 3")

        assertThat(log).hasSize(3)

        assertThatThrownBy { assertThat(log).hasSize(4) }
                .isInstanceOf(AssertionError::class.java)
                .hasMessageContaining("\nExpected size:<4> but was:<3> in:")
    }

    @Test
    fun shouldAssertLogIsEmpty() {
        assertThat(log).isEmpty()

        LOG.info("test message")

        assertThatThrownBy { assertThat(log).isEmpty() }
                .isInstanceOf(AssertionError::class.java)
                .hasMessageContaining("\nExpecting empty but was:<")
    }

    @Test
    fun shouldAssertNoInfoEvents() {
        assertThat(log).hasNoInfo()

        LOG.info("test message")

        assertThatThrownBy { assertThat(log).hasNoInfo() }
                .isInstanceOf(AssertionError::class.java)
                .hasMessageContaining("\nExpecting null or empty but was:<")
    }

    @Test
    fun shouldAssertNoWarnEvents() {
        assertThat(log).hasNoWarn()

        LOG.warn("test message")

        assertThatThrownBy { assertThat(log).hasNoWarn() }
                .isInstanceOf(AssertionError::class.java)
                .hasMessageContaining("\nExpecting null or empty but was:<")
    }

    @Test
    fun shouldAssertNoErrorEvents() {
        assertThat(log).hasNoError()

        LOG.error("test message")

        assertThatThrownBy { assertThat(log).hasNoError() }
                .isInstanceOf(AssertionError::class.java)
                .hasMessageContaining("\nExpecting null or empty but was:<")
    }

    @Test
    fun shouldAssertLogIsNotEmpty() {
        assertThatThrownBy { assertThat(log).isNotEmpty() }
                .isInstanceOf(AssertionError::class.java)
                .hasMessage("\nExpecting actual not to be empty")

        LOG.info("test message")

        assertThat(log).isNotEmpty()
    }

    @Test
    fun shouldCaptureLogEvents() {
        val expectedException = RuntimeException("expected")
        val expectedMessage = "test message"

        LOG.debug(expectedMessage, expectedException)

        assertThat(log)
                .hasEvent(Level.DEBUG)
                .hasEvent(Level.DEBUG) { it.message.isNotEmpty() }
                .hasEvent(Level.DEBUG, expectedMessage)
                .hasEvent(Level.DEBUG, expectedMessage, expectedException)
                .hasEvent(Level.DEBUG, expectedMessage, RuntimeException::class.java)
                .hasEvent(Level.DEBUG, expectedMessage, RuntimeException::class)
                .hasEventContaining(Level.DEBUG, "test", "message")
                .hasEventMatching(Level.DEBUG, Regex("[a-z]+ message"))
                .hasEventMatching(Level.DEBUG, Regex("[a-z]+ message"), expectedException)
                .hasEventMatching(Level.DEBUG, Regex("[a-z]+ message"), RuntimeException::class.java)
                .hasEventMatching(Level.DEBUG, Regex("[a-z]+ message"), RuntimeException::class)
    }

    @Test
    fun shouldCaptureInfoLogEvents() {
        val expectedException = RuntimeException("test exception")
        val expectedMessage = "test message"

        LOG.info(expectedMessage, expectedException)

        assertThat(log)
                .hasInfo()
                .hasInfo { it.message.isNotEmpty() }
                .hasInfo(expectedMessage)
                .hasInfo(expectedMessage, expectedException)
                .hasInfo(expectedMessage, RuntimeException::class)
                .hasInfoContaining("test", "message")
                .hasInfoMatching(Regex("[a-z]+ message"))
                .hasInfoMatching(Regex("[a-z]+ message"))
                .hasInfoMatching(Regex("[a-z]+ message"), expectedException)
                .hasInfoMatching(Regex("[a-z]+ message"), RuntimeException::class)
    }

    @Test
    fun shouldCaptureWarnLogEvents() {
        val expectedException = RuntimeException("test exception")
        val expectedMessage = "test message"

        LOG.warn(expectedMessage, expectedException)

        assertThat(log)
                .hasWarn()
                .hasWarn { it.message.isNotEmpty() }
                .hasWarn(expectedMessage)
                .hasWarn(expectedMessage, expectedException)
                .hasWarn(expectedMessage, RuntimeException::class)
                .hasWarnContaining("test", "message")
                .hasWarnMatching(Regex("[a-z]+ message"))
                .hasWarnMatching(Regex("[a-z]+ message"))
                .hasWarnMatching(Regex("[a-z]+ message"), expectedException)
                .hasWarnMatching(Regex("[a-z]+ message"), RuntimeException::class)
    }

    @Test
    fun shouldCaptureWarnErrorEvents() {
        val expectedException = RuntimeException("test exception")
        val expectedMessage = "test message"

        LOG.error(expectedMessage, expectedException)

        assertThat(log)
                .hasError()
                .hasError { it.message.isNotEmpty() }
                .hasError(expectedMessage)
                .hasError(expectedMessage, expectedException)
                .hasError(expectedMessage, RuntimeException::class)
                .hasErrorContaining("test", "message")
                .hasErrorMatching(Regex("[a-z]+ message"))
                .hasErrorMatching(Regex("[a-z]+ message"))
                .hasErrorMatching(Regex("[a-z]+ message"), expectedException)
                .hasErrorMatching(Regex("[a-z]+ message"), RuntimeException::class)
    }

    @Test
    fun shouldFailAssertionWhenLevelIsNotFound() {
        LOG.info("test message")

        assertThatThrownBy { assertThat(log).hasWarn() }
                .isInstanceOf(AssertionError::class.java)
                .hasMessage("\nExpecting:\n"
                        + " <\"[INFO] test message\">\n"
                        + "to contain:\n"
                        + " <\"[WARN]\"> ")
    }

    @Test
    fun shouldFailAssertionWhenPredicateDoesNotMatch() {
        LOG.info("test message")

        assertThatThrownBy { assertThat(log).hasInfo { it.message.isEmpty() } }
                .isInstanceOf(AssertionError::class.java)
                .hasMessage("\nExpecting:\n"
                        + " <\"[INFO] test message\">\n"
                        + "to contain:\n"
                        + " <\"INFO event matching given predicate\"> ")
    }

    @Test
    fun shouldFailAssertionWhenMessageIsNotFound() {
        LOG.info("test message")

        assertThatThrownBy { assertThat(log).hasInfo("other message") }
                .isInstanceOf(AssertionError::class.java)
                .hasMessage("\nExpecting:\n"
                        + " <\"[INFO] test message\">\n"
                        + "to contain:\n"
                        + " <\"[INFO] other message\"> ")
    }

    @Test
    fun shouldCorrectlyConstructFauilureMessageWithPercentage() {
        LOG.info("test message %")

        assertThatThrownBy { assertThat(log).hasInfo("other message %") }
                .isInstanceOf(AssertionError::class.java)
                .hasMessage("\nExpecting:\n"
                        + " <\"[INFO] test message %\">\n"
                        + "to contain:\n"
                        + " <\"[INFO] other message %\"> ")
    }

    @Test
    fun shouldFailAssertionWhenMessageWithLevelIsNotFound() {
        LOG.info("test message")

        assertThatThrownBy { assertThat(log).hasError("other message") }
                .isInstanceOf(AssertionError::class.java)
                .hasMessage("\n"
                        + "Expecting:\n"
                        + " <\"[INFO] test message\">\n"
                        + "to contain:\n"
                        + " <\"[ERROR] other message\"> ")
    }

    @Test
    fun shouldFailAssertionWhenThrowableIsNotFound() {
        LOG.info("test message")

        assertThatThrownBy { assertThat(log).hasInfo("test message", RuntimeException()) }
                .isInstanceOf(AssertionError::class.java)
                .hasMessage("\n"
                        + "Expecting:\n"
                        + " <\"[INFO] test message\">\n"
                        + "to contain:\n"
                        + " <\"[INFO] test message\"> ")
    }

    @Test
    fun shouldFailAssertionWhenThrowableClassIsNotFound() {
        LOG.info("test message")

        assertThatThrownBy { assertThat(log).hasInfo("test message", RuntimeException::class) }
                .isInstanceOf(AssertionError::class.java)
                .hasMessage("\n"
                        + "Expecting:\n"
                        + " <\"[INFO] test message\">\n"
                        + "to contain:\n"
                        + " <\"[INFO] test message\"> ")
    }

    @Test
    fun shouldFailAssertionWhenMessageDoesNotContain() {
        LOG.info("test message")

        assertThatThrownBy { assertThat(log).hasInfoContaining("other", "message") }
                .isInstanceOf(AssertionError::class.java)
                .hasMessage("\nExpecting:\n"
                        + " <\"[INFO] test message\">\n"
                        + "to contain:\n"
                        + " <\"INFO message containing [other, message]\"> ")
    }

    @Test
    fun shouldFailAssertionWhenMessageDoesNotMatch() {
        LOG.info("test message")

        assertThatThrownBy { assertThat(log).hasInfoMatching(Regex("[0-9]+ message")) }
                .isInstanceOf(AssertionError::class.java)
                .hasMessage("\nExpecting:\n"
                        + " <\"[INFO] test message\">\n"
                        + "to contain:\n"
                        + " <\"INFO message matching: [0-9]+ message\"> ")
    }
}
