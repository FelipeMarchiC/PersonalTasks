package bes.mobile.personaltasks.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String = "",
    var description: String = "",
    var dueDate: Date
): Parcelable
