package com.trevorwiebe.trackacow.presentation.archives

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.presentation.archives.ArchiveRvAdapter.ArchiveViewHolder

class ArchiveRvAdapter : RecyclerView.Adapter<ArchiveViewHolder>() {

    var archivedLotEntities: List<LotModel> = emptyList()

    override fun getItemCount(): Int {
        return archivedLotEntities.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ArchiveViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_archived_lot, viewGroup, false)
        return ArchiveViewHolder(view)
    }

    override fun onBindViewHolder(archiveViewHolder: ArchiveViewHolder, i: Int) {
        val archivedLotEntity = archivedLotEntities[i]
        val name = archivedLotEntity.lotName
        val date = archivedLotEntity.dateArchived!!
        val strDate = Utility.convertMillisToDate(date)
        archiveViewHolder.mArchiveLotName.text = name
        val dateString = "Archived: $strDate"
        archiveViewHolder.mDateArchived.text = dateString
    }

    fun swapArchivedLots(archivedLotModels: List<LotModel>) {
        this.archivedLotEntities = archivedLotModels
        notifyDataSetChanged()
    }

    inner class ArchiveViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mArchiveLotName: TextView
        val mDateArchived: TextView

        init {
            mArchiveLotName = view.findViewById(R.id.archived_lot_name)
            mDateArchived = view.findViewById(R.id.date_archived)
        }
    }
}