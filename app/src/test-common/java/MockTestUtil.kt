import com.mustafa.tandem.model.Member

class MockTestUtil {
    companion object {

        fun createMembers(count: Int): List<Member> {
            return (0 until count).map {
                Member(1, 1 ,"Mustafa$it", listOf("DE$it"), listOf("EN$it"), "URL$it", it, "TOPIC$it")
            }
        }
    }
}