package app.app.astrocoin.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import app.app.astrocoin.R
import app.app.astrocoin.adapters.AdapterUserSearch
import app.app.astrocoin.models.UserRequest
import app.app.astrocoin.sampleclass.ApiClient
import com.google.gson.Gson
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchUserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_searchuser, container, false)
    }

    private var sharedPreferences: SharedPreferences? = null
    var token: String? = null
    var dataModalArrayList: ArrayList<UserRequest>? = null
    var adapter: AdapterUserSearch? = null
    var listViewSearch: ListView? = null
    var searchProgressBar: ProgressBar? = null
    private var userSearchView: SearchView? = null
    var id =
        ""
    var name: String? = ""
    var lastname: String? = ""
    var stack: String? = ""
    var photo: String? = ""
    var qwaSar: String? = ""
    var status: String? = ""
    var verify: String? = ""
    var balance: String? = ""
    var wallet: String? = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences =
            requireActivity().getSharedPreferences(
                requireContext().getString(R.string.astrocoin),
                Context.MODE_PRIVATE
            )
        dataModalArrayList = ArrayList()
        listViewSearch = view.findViewById(R.id.listViewsearch)
        listViewSearch?.divider = null
        listViewSearch?.dividerHeight = 20
        searchProgressBar = view.findViewById(R.id.searchprogressBar)
        adapter = AdapterUserSearch(dataModalArrayList!!, requireActivity())
        getUserData()
        listViewSearch?.adapter = adapter
        userSearchView = view.findViewById(R.id.usersearchView)
        userSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter?.filter?.filter(newText)
                return false
            }
        })

    }

    private fun getUserData() {
        token = sharedPreferences?.getString("token", "")
        getData()
    }

    private fun getData() {
        searchProgressBar!!.visibility = View.VISIBLE
        val call: Call<Any> = ApiClient.userService.userSearchRequest("Bearer $token")
        call.enqueue(object : Callback<Any?> {
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                if (response.isSuccessful) {
                    val gson = Gson()
                    val json = gson.toJson(response.body())
                    val parser = JsonParser()
                    val array = parser.parse(json).asJsonArray
                    for (element in array) {
                        val `object` = element.asJsonObject
                        id = `object`["id"].asString
                        name = `object`["name"].asString
                        lastname = `object`["last_name"].asString
                        stack = `object`["stack"].asString
                        photo = if (`object`["photo"] != null) {
                            `object`["photo"].asString
                        } else {
                            ""
                        }
                        qwaSar = `object`[requireContext().getString(R.string.qwasar)].asString
                        status = `object`["status"].asString
                        verify = `object`["verify"].asString
                        balance = `object`["balance"].asString
                        wallet = `object`["wallet"].asString
                        dataModalArrayList!!.add(
                            UserRequest(
                                id, name!!, lastname!!, stack!!,
                                photo!!, qwaSar!!, status!!, verify!!, balance!!, wallet!!
                            )
                        )
                    }
                    listViewSearch!!.adapter = adapter
                    searchProgressBar!!.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<Any?>, t: Throwable) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                call.cancel()
            }
        })
    }
}