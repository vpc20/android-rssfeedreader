package com.example.rssfeedreader.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.rssfeedreader.R
import com.example.rssfeedreader.database.RssDatabase
import com.example.rssfeedreader.databinding.RssFragmentBinding
import com.example.rssfeedreader.databinding.RssItemsBinding
import com.example.rssfeedreader.ui.RssViewModel.RssData
import java.util.*
import java.util.concurrent.TimeUnit


class RssFragment : Fragment() {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
//        Log.i("RssFeedReader", "RssFragment - onCreateView")
        val binding: RssFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.rss_fragment, container, false)
        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application
        val dataSource = RssDatabase.getInstance(application).rssDatabaseDao

        navController = this.findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(
            this.activity as AppCompatActivity,
            navController,
            appBarConfiguration
        )

        val viewModelFactory = RssFeedViewModelFactory(dataSource, application)
        val rssViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(RssViewModel::class.java)


        val rssRecyclerView = binding.rssRecyclerView
        val rssListAdapter = RssListAdapter()
//            RssListAdapter(RssListAdapter.OnLongClickListener { item: RssData ->
//                navController.navigate(
//                    RssFragmentDirections.actionRssFragmentToWebViewerFragment(
//                        item.link!!
//                    )
//                )
//            })
        rssRecyclerView.adapter = rssListAdapter

        setHasOptionsMenu(true)

        val swipeRefreshLayout: SwipeRefreshLayout = binding.rssSwipeRefreshLayout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            swipeRefreshLayout.setDistanceToTriggerSync(400)
        swipeRefreshLayout.setOnRefreshListener {
            rssViewModel.getRssFeeds()
            swipeRefreshLayout.isRefreshing = false
        }

        rssViewModel.rssDataList.observe(viewLifecycleOwner, Observer {
            it.let {
                //                Log.i("RssFeedReader", " RssFragment - rssViewModel.rssDataList.observe")
                rssListAdapter.submitList(it)
                rssListAdapter.notifyDataSetChanged()
            }
        })
        rssViewModel.status.observe(viewLifecycleOwner, Observer {
            //            Log.i("RssFeedReader", " RssFragment - rssViewModel.status.observe")
            when (it) {
                RssApiStatus.LOADING -> {
                    binding.cloudOffImg.visibility = View.VISIBLE
                    binding.cloudOffImg.setImageResource(R.drawable.loading_animation)
                }
                RssApiStatus.ERROR -> {
                    binding.cloudOffImg.visibility = View.VISIBLE
                    binding.cloudOffImg.setImageResource(R.drawable.ic_cloud_off_24dp)
                }
                RssApiStatus.DONE -> {
                    binding.cloudOffImg.visibility = View.GONE
                }
                else -> {
                }
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, view!!.findNavController())
                || super.onOptionsItemSelected(item)
    }


}

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        rssViewModel.createTestData()
//    }

//class RssListAdapter(private val onClickListener: OnLongClickListener) :
class RssListAdapter :
    ListAdapter<RssData, RssListAdapter.ViewHolder>(FileListDiffCallback()) {

    private lateinit var binding: RssItemsBinding

    class ViewHolder(private val binding: RssItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RssData) {
//            Log.i("RssFeedReader", "RssListAdapter - ViewHolder - bind")
            binding.title.text = item.title
            binding.pubDurationDays.text = getDurationDays(item.pubDate)
            binding.siteName.text = item.siteName
            Glide.with(binding.rssImage.context)
                .load(item.ogImg)
                .into(binding.rssImage)
        }

        private fun getDurationDays(pubDateStr: String?): String {
            //pubDateStr sample "Mon, 30 Dec 2019 09:19:00 +0000"
            val pubDate = Date(pubDateStr).time
            val dateToday = Calendar.getInstance().time.time
            val durationDays = TimeUnit.DAYS.convert(dateToday - pubDate, TimeUnit.MILLISECONDS)
            return if (durationDays < 1) {
                val durationHours =
                    TimeUnit.HOURS.convert(dateToday - pubDate, TimeUnit.MILLISECONDS)
                if (durationHours < 1) {
                    "·  ${TimeUnit.MINUTES.convert(
                        dateToday - pubDate,
                        TimeUnit.MILLISECONDS
                    )}m"
                } else {
                    "·  ${durationHours}h"
                }
            } else
                "·  ${durationDays}d"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        Log.i("RssFeedReader", "onCreateViewHolder")
        binding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rss_items,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            //            onClickListener.onClick(item)
            val myUri = Uri.parse(item.link)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = myUri
            val bundle = null
            startActivity(it.context, intent, bundle)
        }
        holder.bind(item)
    }


//    class OnLongClickListener(val clickListener: (item: RssData) -> Unit) {
//        fun onClick(item: RssData) = clickListener(item)
//    }


    class FileListDiffCallback : DiffUtil.ItemCallback<RssData>() {
        override fun areItemsTheSame(
            oldItem: RssData,
            newItem: RssData
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: RssData,
            newItem: RssData
        ): Boolean {
            return oldItem == newItem
        }
    }
}

