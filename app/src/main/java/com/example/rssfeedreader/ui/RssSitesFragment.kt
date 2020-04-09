package com.example.rssfeedreader.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rssfeedreader.R
import com.example.rssfeedreader.database.RssDatabase
import com.example.rssfeedreader.database.RssSites
import com.example.rssfeedreader.database.RssSitesDao
import com.example.rssfeedreader.databinding.RssSitesFragmentBinding
import com.example.rssfeedreader.databinding.RssSitesItemsBinding
import kotlinx.android.synthetic.main.rss_sites_fragment.view.*
import kotlinx.android.synthetic.main.rss_sites_items.view.rssSitename
import kotlinx.android.synthetic.main.rss_sites_items.view.rssUrl

class RssSitesFragment : Fragment() {

    private lateinit var binding: RssSitesFragmentBinding
    private lateinit var rssSitesViewModel: RssSitesViewModel
    private lateinit var rssSitesRecyclerView: RecyclerView
    private lateinit var dataSource: RssSitesDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        Log.i("RssFeedReader", "RssSitesFragment - onCreateView")
        binding = DataBindingUtil.inflate(inflater, R.layout.rss_sites_fragment, container, false)
        binding.lifecycleOwner = this

//        navController = this.findNavController()

        val application = requireNotNull(this.activity).application
        dataSource = RssDatabase.getInstance(application).rssDatabaseDao
        val viewModelFactory = RssSitesViewModelFactory(dataSource, application)
//        val rssSitesViewModel =
//            ViewModelProviders.of(this, viewModelFactory).get(RssSitesViewModel::class.java)
        rssSitesViewModel =
            ViewModelProviders.of(this, viewModelFactory)[RssSitesViewModel::class.java]

        rssSitesRecyclerView = binding.rssSitesRecyclerView
//        val rssSitesListAdapter = RssSitesListAdapter()
        val rssSitesListAdapter =
            RssSitesListAdapter(RssSitesListAdapter.OnLongClickListener { v: View ->
                showRssSitesPopup(v)
            })
        rssSitesRecyclerView.adapter = rssSitesListAdapter


        binding.addRssSiteFab.setOnClickListener {
            this.findNavController().navigate(
                RssSitesFragmentDirections.actionRssSitesFragmentToRssEditFragment("add", "", "")
            )
        }

//        not working
//        binding.rssSitesConstraintLayout.setOnClickListener {
//            Log.i("RssFeedReader", "binding.rssSitesConstraintLayout.setOnClickListener")
//            showRssSitesPopup(it)
//        }

        rssSitesViewModel.rssSitesList.observe(viewLifecycleOwner, Observer {
            it.let {
                //                Log.i("RssFeedReader", " RssFragment - rssViewModel.rssDataList.observe")
                rssSitesListAdapter.submitList(it)
                rssSitesListAdapter.notifyDataSetChanged()
            }
        })

        rssSitesViewModel.refreshRssSitesList(dataSource)
        return binding.root
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.rssSitesRecyclerView.hideKeyboard()
        registerForContextMenu(rssSitesRecyclerView)
    }

//    override fun onCreateContextMenu(
//        menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?
//    ) {
//        super.onCreateContextMenu(menu, v, menuInfo)
//        activity!!.menuInflater.inflate(R.menu.rss_sites_popup_menu, menu)
//    }
//
//    override fun onContextItemSelected(item: MenuItem): Boolean {
////        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
//        return when (item.itemId) {
//            R.id.updateRssSite -> {
//                true
//            }
//            R.id.deleteRssSite -> {
//                val alertDialog: AlertDialog? = activity?.let {
//                    val builder = AlertDialog.Builder(it)
//                    builder.apply {
//                        setTitle("Delete Confirmation")
//                        setMessage("Delete the RSS Site ?")
//                        setPositiveButton("Delete") { dialog, id ->
//                            rssSitesViewModel.deleteRssSite(
//                                RssSites(
//                                    binding.rssSitesRecyclerView.rssSitename.text.toString(),
//                                    binding.rssSitesRecyclerView.rssUrl.text.toString()
//                                )
//                            )
//                            it.findNavController(R.id.rssEditConstraintLayout).navigateUp()
//                        }
//                        setNegativeButton("Cancel") { dialog, id ->
//                            Unit
//                        }
//                    }
//                    builder.create()
//                }
//                alertDialog!!.show()
//                true
//            }
//            else -> super.onContextItemSelected(item)
//        }
//    }

    private fun showRssSitesPopup(v: View) {
//        Log.i("RssFeedReader", "showRssSitesPopup")
//        val rssSitesPopup = PopupMenu(context!!, v)
//        rssSitesPopup.menuInflater.inflate(R.menu.rss_sites_popup_menu, rssSitesPopup.menu)
//        rssSitesPopup.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.updateRssSite -> {
//                    v.findNavController().navigate(
//                        RssSitesFragmentDirections
//                            .actionRssSitesFragmentToRssEditFragment(
//                                item.rssSiteName,
//                                item.rssUrl
//                            )
//                    )
//                }
//                R.id.deleteRssSite -> deleteAlertDialog(item)
//            }
//            true
//        }
//        rssSitesPopup.show()
        val popupMenu = PopupMenu(context!!, v).apply {
            menuInflater.inflate(R.menu.rss_sites_popup_menu, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.updateRssSite -> {
                        v.findNavController().navigate(
                            RssSitesFragmentDirections
                                .actionRssSitesFragmentToRssEditFragment(
                                    "update",
                                    v.rssSitename.text.toString(),
                                    v.rssUrl.text.toString()

                                )
                        )
                    }
                    R.id.deleteRssSite -> deleteAlertDialog(v)
                }
                true
            }
        }

        val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
        fieldMPopup.isAccessible = true
        val mPopup = fieldMPopup.get(popupMenu)
        mPopup.javaClass
            .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
            .invoke(mPopup, true)
        popupMenu.show()

    }

    private fun deleteAlertDialog(v: View) {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle("Delete Confirmation")
                setMessage("Delete the RSS Site ${v.rssSitename.text} ?")
                setPositiveButton("Delete") { _, _ ->
                    rssSitesViewModel.deleteRssSite(
                        RssSites(
                            v.rssSitename.text.toString(),
                            v.rssUrl.text.toString()
                        )
                    )
                    rssSitesViewModel.refreshRssSitesList(dataSource)
                }
                setNegativeButton("Cancel") { _, _ ->
                    Unit
                }
            }
            builder.create()
        }
        alertDialog!!.show()
    }

}

class RssSitesListAdapter(private val onLongClickListener: OnLongClickListener) :
    ListAdapter<RssSites, RssSitesListAdapter.ViewHolder>(FileListDiffCallback()) {

    private lateinit var binding: RssSitesItemsBinding

    class ViewHolder(private val binding: RssSitesItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RssSites) {
//            Log.i("RssFeedReader", "RssListAdapter - ViewHolder - bind")
            binding.rssSitename.text = item.rssSiteName
            binding.rssUrl.text = item.rssUrl
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        Log.i("RssFeedReader", "onCreateViewHolder")
        binding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rss_sites_items,
                parent,
                false
            )

//        binding.rssSitesItemsConstraintLayout.setOnClickListener {
//            Log.i("RssFeedReader", "binding.rssSitesItemsConstraintLayout.setOnClickListener ")
//        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnLongClickListener {
            onLongClickListener.onLongClick(holder.itemView)
            true
        }
        holder.bind(item)
    }

    class OnLongClickListener(val longClickListener: (v: View) -> Unit) {
        fun onLongClick(v: View) = longClickListener(v)
    }

    class FileListDiffCallback : DiffUtil.ItemCallback<RssSites>() {
        override fun areItemsTheSame(
            oldItem: RssSites,
            newItem: RssSites
        ): Boolean {
            return oldItem.rssSiteName == newItem.rssSiteName
        }

        override fun areContentsTheSame(
            oldItem: RssSites,
            newItem: RssSites
        ): Boolean {
            return oldItem == newItem
        }
    }

}

//class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnCreateContextMenuListener {
//
//    var icon: ImageView
//
//    var fileName: TextView
//    var menuButton: ImageButton
//
//
//    init {
//        icon = v.findViewById(R.id.file_icon) as ImageView
//        fileName = v.findViewById(R.id.file_name) as TextView
//        menuButton = v.findViewById(R.id.menu_button) as ImageButton
//        v.setOnCreateContextMenuListener(this)
//    }
//
//    fun onCreateContextMenu(
//        menu: ContextMenu, v: View,
//        menuInfo: ContextMenu.ContextMenuInfo
//    ) {
//        //menuInfo is null
//        menu.add(
//            Menu.NONE, R.id.ctx_menu_remove_backup,
//            Menu.NONE, R.string.remove_backup
//        )
//        menu.add(
//            Menu.NONE, R.id.ctx_menu_restore_backup,
//            Menu.NONE, R.string.restore_backup
//        )
//    }
//}
