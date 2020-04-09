package com.example.rssfeedreader.ui

import android.app.Application
import android.os.Bundle
import android.view.*
import android.webkit.URLUtil
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.rssfeedreader.R
import com.example.rssfeedreader.database.RssDatabase
import com.example.rssfeedreader.database.RssSites
import com.example.rssfeedreader.databinding.RssEditFragmentBinding

class RssEditFragment : Fragment() {

    private lateinit var rssSitesViewModel: RssSitesViewModel
    private val args: RssEditFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
//        Log.i("RssFeedReader", "RssEditFragment - onCreateView")

        val binding: RssEditFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.rss_edit_fragment, container, false)
        binding.lifecycleOwner = this

        val application: Application = requireNotNull(this.activity).application
        val dataSource = RssDatabase.getInstance(application).rssDatabaseDao

        val navController = this.findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(
            this.activity as AppCompatActivity, navController, appBarConfiguration
        )

//        val viewModelFactory = RssEditViewModelFactory(dataSource, application)
//        val rssSitesViewModel =
//            ViewModelProviders.of(this, viewModelFactory).get(RssEditViewModel::class.java)
        val viewModelFactory = RssSitesViewModelFactory(dataSource, application)
        rssSitesViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(RssSitesViewModel::class.java)

//        val alertDialogBuilder = activity.let{
//            val builder = AlertDialog.Builder(it)
//        }
//        val alertDialog = AlertDialog.Builder(context as Context).create()

        setHasOptionsMenu(true)

        binding.rssSiteNameEdit.setText(args.rssSiteName)
        binding.rssUrlEdit.setText(args.rssUrl)

        binding.saveButton.setOnClickListener {
            val rssSiteNameStr = binding.rssSiteNameEdit.text.toString()
            val rssUrlStr = binding.rssUrlEdit.text.toString()
            if (validRssEntries(rssSiteNameStr, rssUrlStr)) {
                if (args.dbOperationType == "add")
                    rssSitesViewModel.addRssSite(
                        RssSites(
                            binding.rssSiteNameEdit.text.toString(),
                            binding.rssUrlEdit.text.toString()
                        )
                    )
                else {
                    rssSitesViewModel.deleteRssSite(
                        RssSites(args.rssSiteName, args.rssUrl)
                    )
                    rssSitesViewModel.addRssSite(
                        RssSites(
                            binding.rssSiteNameEdit.text.toString(),
                            binding.rssUrlEdit.text.toString()
                        )
                    )
                }

                navController.navigateUp()
            }
        }

        return binding.root

    }

    private fun validRssEntries(rssSiteNameStr: String, rssUrlStr: String): Boolean {
        if (rssSiteNameStr.trim() == "" || rssUrlStr.trim() == "") {
            val builder = AlertDialog.Builder(activity!!)
            builder.apply {
                setTitle("Message")
                setMessage("Entries cannot be blanks")
                setPositiveButton("Ok") { _, _ -> }
            }.create().show()
            return false
//            val alertDialog: AlertDialog? = activity?.let {
//                val builder = AlertDialog.Builder(it)
//                builder.apply {
//                    setTitle("Message")
//                    setMessage("Entries cannot be blanks")
//                    setPositiveButton("Ok") { _, _ ->
//                        Unit
//                    }
//                }
//                builder.create()
//            }
//            alertDialog!!.show()
//            return false
        }

        if (!URLUtil.isValidUrl(rssUrlStr)) {
            val alertDialog: AlertDialog? = activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setTitle("Message")
                    setMessage("Invalid Url")
                    setPositiveButton("Ok") { _, _ ->
                        Unit
                    }
                }
                builder.create()
            }
            alertDialog!!.show()
            return false
        }
        return true
    }


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.rss_edit_menu, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.deleteSite -> {
//                val alertDialog: AlertDialog? = activity?.let {
//                    val builder = AlertDialog.Builder(it)
//                    builder.apply {
//                        setTitle("Delete Confirmation")
//                        setMessage("Delete the RSS Site ?")
//                        setPositiveButton("Delete") { dialog, id ->
//                            rssSitesViewModel.deleteRssSite(
//                                RssSites(args.rssSiteName, args.rssUrl)
//                            )
//                            it.findNavController(R.id.rssEditConstraintLayout).navigateUp()
//                        }
//                        setNegativeButton("Cancel") { dialog, id ->
//                            Log.i("test", "test")
//                        }
//                    }
//                    builder.create()
//                }
//                alertDialog!!.show()
//                true
//            }
//            else -> true
//        }
////        return super.onOptionsItemSelected(item)
//    }

}
