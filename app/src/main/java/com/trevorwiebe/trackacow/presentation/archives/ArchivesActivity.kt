package com.trevorwiebe.trackacow.presentation.archives

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trevorwiebe.trackacow.R
import com.trevorwiebe.trackacow.domain.adapters.ArchiveRvAdapter
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.ItemClickListener
import com.trevorwiebe.trackacow.presentation.lot_reports.LotReportActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArchivesActivity : AppCompatActivity() {

    private val archiveRvAdapter = ArchiveRvAdapter()
    private var archivedLotEntities: MutableList<LotModel> = mutableListOf()
    private lateinit var mNoArchives: TextView

    private val mArchivesViewModel: ArchivesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archives)

        mNoArchives = findViewById(R.id.no_archives)
        val archiveRv = findViewById<RecyclerView>(R.id.archive_rv)
        archiveRv.layoutManager = LinearLayoutManager(this)
        archiveRv.adapter = archiveRvAdapter
        archiveRv.addOnItemTouchListener(
            ItemClickListener(
                this,
                archiveRv,
                object : ItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val (_, _, lotId) = archivedLotEntities[position]
                        val archiveReportsIntent =
                            Intent(this@ArchivesActivity, LotReportActivity::class.java)
                        archiveReportsIntent.putExtra("lotId", lotId)
                        archiveReportsIntent.putExtra("reportType", Constants.ARCHIVE)
                        startActivity(archiveReportsIntent)
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mArchivesViewModel.uiState.collect {

                }
            }
        }
    }

//    override fun onArchivedLotsLoaded(archivedLotEntities: ArrayList<ArchivedLotEntity>) {
//        this.archivedLotEntities = archivedLotEntities
//        if (archivedLotEntities.size == 0) {
//            mNoArchives.visibility = View.VISIBLE
//        } else {
//            mNoArchives.visibility = View.INVISIBLE
//        }
//        archiveRvAdapter.swapArchivedLots(archivedLotEntities)
//    }

}