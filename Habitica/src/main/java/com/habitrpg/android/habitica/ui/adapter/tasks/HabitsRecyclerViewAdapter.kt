package com.habitrpg.android.habitica.ui.adapter.tasks

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.habitrpg.android.habitica.ui.viewHolders.tasks.HabitViewHolder
import com.habitrpg.android.habitica.ui.viewmodels.TasksViewModel

class HabitsRecyclerViewAdapter(layoutResource: Int, viewModel: TasksViewModel) : RealmBaseTasksRecyclerViewAdapter(layoutResource, viewModel) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            HabitViewHolder(
                getContentView(parent),
                { task, direction -> taskScoreEvents?.invoke(task, direction) },
                { task, view ->
                    taskOpenEvents?.invoke(task, view)
                },
                { task ->
                    brokenTaskEvents?.invoke(task)
                },
                viewModel
            )
        } else {
            super.onCreateViewHolder(parent, viewType)
        }
    }
}
