import com.harvey.nuandsu.R
import android.os.Bundle
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StatusActivity : AppCompatActivity() {
    private var lastCheckedId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dashboard)

        val radioGroup: RadioGroup = findViewById(R.id.status_group)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == -1) return@setOnCheckedChangeListener

            if (checkedId == lastCheckedId) {
                group.clearCheck()
                lastCheckedId = -1
            } else {
                lastCheckedId = checkedId
            }
        }
    }


}
