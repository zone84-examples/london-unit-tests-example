package tech.zone84.examples.london

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.throwable.shouldHaveMessage
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import tech.zone84.examples.london.Initialization.initialize

class InitializationSpec : ShouldSpec({
    should("call onStart for all initializer when the all of them start successfully") {
        // given
        val initializers = listOf<Initializer>(
            mock(),
            mock(),
            mock()
        )

        // when
        initialize(initializers)

        // then
        inOrder(*initializers.toTypedArray()) {
            verify(initializers[0]).onStart()
            verify(initializers[1]).onStart()
            verify(initializers[2]).onStart()
        }
        verify(initializers[0], never()).onRollback()
        verify(initializers[1], never()).onRollback()
        verify(initializers[2], never()).onRollback()
    }

    should("not call anything else when onStart of the very first initializers fails") {
        // given
        val expectedError = RuntimeException("test")
        val initializers = listOf<Initializer>(
            mock {
                on { onStart() } doThrow expectedError
            },
            mock(),
            mock()
        )

        // when
        val exception = shouldThrow<RuntimeException> {
            initialize(initializers)
        }

        // then
        exception shouldHaveMessage("test")
        verify(initializers[0]).onStart()
        verify(initializers[1], never()).onStart()
        verify(initializers[2], never()).onStart()
        verify(initializers[0], never()).onRollback()
        verify(initializers[1], never()).onRollback()
        verify(initializers[2], never()).onRollback()
    }

    should("call onStart for previous initializers and onRollback in reverse order when the middle initializer fails") {
        // given
        val expectedError = RuntimeException("test")
        val initializers = listOf<Initializer>(
            mock(),
            mock(),
            mock {
                on { onStart() } doThrow expectedError
            },
            mock()
        )

        // when
        val exception = shouldThrow<RuntimeException> {
            initialize(initializers)
        }

        // then
        exception shouldHaveMessage("test")
        inOrder(*initializers.toTypedArray()) {
            verify(initializers[0]).onStart()
            verify(initializers[1]).onStart()
            verify(initializers[2]).onStart()
            verify(initializers[1]).onRollback()
            verify(initializers[0]).onRollback()
        }
        verifyNoInteractions(initializers[3])
    }

    should("call onRollback for all initializers but the last when the last initializer fails") {
        // given
        val expectedError = RuntimeException("test")
        val initializers = listOf<Initializer>(
            mock(),
            mock(),
            mock {
                on { onStart() } doThrow expectedError
            }
        )

        // when
        val exception = shouldThrow<RuntimeException> {
            initialize(initializers)
        }

        // then
        exception shouldHaveMessage("test")
        inOrder(*initializers.toTypedArray()) {
            verify(initializers[0]).onStart()
            verify(initializers[1]).onStart()
            verify(initializers[2]).onStart()
            verify(initializers[1]).onRollback()
            verify(initializers[0]).onRollback()
        }
        verify(initializers[2], never()).onRollback()
    }

    should("still call all onRollback even when one of them fails") {
        // given
        val onStartException = RuntimeException("onStart")
        val onRollbackException = RuntimeException("onRollback")
        val initializers = listOf<Initializer>(
            mock(),
            mock {
                on { onRollback() } doThrow onRollbackException
            },
            mock(),
            mock {
                on { onStart() } doThrow onStartException
            }
        )

        // when
        val exception = shouldThrow<RuntimeException> {
            initialize(initializers)
        }

        // then
        exception shouldHaveMessage("onStart")
        exception.suppressed shouldContain onRollbackException
        inOrder(*initializers.toTypedArray()) {
            verify(initializers[2]).onRollback()
            verify(initializers[1]).onRollback()
            verify(initializers[0]).onRollback()
        }
        verify(initializers[3], never()).onRollback()
    }
})
