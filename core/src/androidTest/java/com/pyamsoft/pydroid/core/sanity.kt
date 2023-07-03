import androidx.test.filters.SmallTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

@SmallTest
public class AndroidSanity {

  @Test public fun sanity(): Unit = runTest { assertEquals(3 + 3, 6) }
}
