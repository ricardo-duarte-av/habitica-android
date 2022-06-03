package com.habitrpg.wearos.habitica.ui.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import com.habitrpg.common.habitica.models.tasks.TaskType
import com.habitrpg.wearos.habitica.R
import com.habitrpg.wearos.habitica.databinding.ActivityMainBinding
import com.habitrpg.wearos.habitica.ui.adapters.HubAdapter
import com.habitrpg.wearos.habitica.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

data class MenuItem(
    val identifier: String,
    val title: String,
    val icon: Drawable?,
    val color: Int,
    val onClick: () -> Unit
)

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    val viewModel: MainViewModel by viewModels()

    private val adapter = HubAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        binding.root.apply {
            layoutManager =
                WearableLinearLayoutManager(this@MainActivity, HabiticaScrollingLayoutCallback())
            adapter = this@MainActivity.adapter
        }
        binding.root.post {
            binding.root.setPaddingRelative(0, (binding.root.height * 0.25).toInt(), 0, (binding.root.height * 0.25).toInt())
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.data = listOf(
            MenuItem(
                "avatar",
                "Avatar",
                AppCompatResources.getDrawable(this, R.drawable.ic_avatar),
                ContextCompat.getColor(this, R.color.brand_400)
            ) {
              openAvatarActivity()
            },
            MenuItem(
                "Stats",
                getString(R.string.stats),
                AppCompatResources.getDrawable(this, R.drawable.ic_stats),
                ContextCompat.getColor(this, R.color.red_100)
            ) {

            },
            MenuItem(
                "habits",
                getString(R.string.habits),
                AppCompatResources.getDrawable(this, R.drawable.icon_habits),
                ContextCompat.getColor(this, R.color.orange_100)
            ) {
              openTasklist(TaskType.HABIT)
            },
            MenuItem(
                "dailies",
                getString(R.string.dailies),
                AppCompatResources.getDrawable(this, R.drawable.icon_dailies),
                ContextCompat.getColor(this, R.color.yellow_100)
            ) {
                openTasklist(TaskType.DAILY)
            },
            MenuItem(
                "todos",
                getString(R.string.todos),
                AppCompatResources.getDrawable(this, R.drawable.icon_todos),
                ContextCompat.getColor(this, R.color.green_100)
            ) {
                openTasklist(TaskType.TODO)
            },
            MenuItem(
                "rewards",
                getString(R.string.rewards),
                AppCompatResources.getDrawable(this, R.drawable.icon_rewards),
                ContextCompat.getColor(this, R.color.teal_100)
            ) {
                openTasklist(TaskType.REWARD)
            },
            MenuItem(
                "settings",
                getString(R.string.settings),
                AppCompatResources.getDrawable(this, R.drawable.ic_settings),
                ContextCompat.getColor(this, R.color.blue_100)
            ) {
            }
        )
        viewModel.user.observe(this) {
            adapter.title = it.profile?.name ?: ""
            adapter.notifyItemChanged(0)
        }
    }

    private fun openAvatarActivity() {
        startActivity(Intent(this, AvatarActivity::class.java))
    }

    private fun openTasklist(type: TaskType) {
        val intent = Intent(this, TaskListActivity::class.java).apply {
            putExtra("task_type", type.value)
        }
        startActivity(intent)
    }
}

private const val MAX_ICON_PROGRESS = 0.8f

class HabiticaScrollingLayoutCallback : WearableLinearLayoutManager.LayoutCallback() {

    private var progressToCenter: Float = 0f

    override fun onLayoutFinished(child: View, parent: RecyclerView) {
        child.apply {
            // Figure out % progress from top to bottom
            val centerOffset = height.toFloat() / 2.0f / parent.height.toFloat()
            val yRelativeToCenterOffset = y / parent.height + centerOffset

            // Normalize for center
            progressToCenter = abs(0.5f - yRelativeToCenterOffset) - 0.25f
            if (progressToCenter < 0) {
                scaleX = 1f
                scaleY = 1f
                alpha = 1f
                return
            }
            // Adjust to the maximum scale
            progressToCenter = Math.min(progressToCenter * 1.5f, MAX_ICON_PROGRESS)

            scaleX = 1 - progressToCenter
            scaleY = 1 - progressToCenter
            alpha = 1 - progressToCenter * 2
        }
    }
}