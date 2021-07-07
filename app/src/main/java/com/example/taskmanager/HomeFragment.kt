package com.example.taskmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.data.TestDbObject
import com.example.taskmanager.data.TestDbViewModel
import kotlinx.android.synthetic.main.home_fragment.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "db"
private const val ARG_PARAM2 = "param2"
private const val LOG_TAG = "1234"


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mDbModel: TestDbViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.home_fragment, container, false)

        mDbModel = ViewModelProvider(this).get(TestDbViewModel::class.java)
        val data = mDbModel.data

        val btn = view.homeFragment_button
        val editText = view.homeFragment_editText
        val dbShower = view.homeFragment_editText_db
        btn.setOnClickListener {
            val txt = editText.text.toString()

            val obj = TestDbObject(0, txt, txt.hashCode().toString())
            mDbModel.addTestDbObj(obj)
            editText.text.clear()
        }

        data.observe(viewLifecycleOwner, {
            if (it.isEmpty()) return@observe

            val obj = it[it.lastIndex]
            val txt = "${obj.id} ${obj.info1} ${obj.info2}"

            dbShower.text = txt
        })

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}