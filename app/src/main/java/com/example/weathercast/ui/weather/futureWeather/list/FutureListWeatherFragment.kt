package com.example.weathercast.ui.weather.futureWeather.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weathercast.R
import com.example.weathercast.data.db.LocalDateConverter
import com.example.weathercast.data.db.unit.future.list.UnitSpecificSimpleFutureWeatherEntry
import com.example.weathercast.ui.base.ScopedFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.current_weather_fragment.group_loading
import kotlinx.android.synthetic.main.future_list_weather_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import org.threeten.bp.LocalDate

class FutureListWeatherFragment : ScopedFragment(), DIAware {

    override val di by closestDI()
    private val viewModelFactory: FutureListWeatherViewModelFactory by instance()

    private lateinit var viewModel: FutureListWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.future_list_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FutureListWeatherViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch(Dispatchers.Main) {
        val futureWeatherEntries = viewModel.weatherEntries.await()
        val weatherLocation = viewModel.weatherLocation.await()

        weatherLocation.observe(viewLifecycleOwner, Observer {location ->
            if (location == null) return@Observer

            updateLocation(location.name)
        })

        futureWeatherEntries.observe(viewLifecycleOwner, Observer {weatherEntries ->
            if (weatherEntries == null) return@Observer

            group_loading.visibility = View.GONE

            updateDateToNextWeek()
            initRecyclerView(weatherEntries.toFutureWeatherItems())
        })
    }

    private fun updateLocation(location: String) {
        (activity as AppCompatActivity).supportActionBar?.title = location
    }

    private fun updateDateToNextWeek() {
        (activity as AppCompatActivity).supportActionBar?.subtitle = "Next week"
    }

    private fun List<UnitSpecificSimpleFutureWeatherEntry>.toFutureWeatherItems() : List<FutureWeatherItem> {
        return this.map {
            FutureWeatherItem(it)
        }
    }

    private fun initRecyclerView(items: List<FutureWeatherItem>) {
        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(items)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FutureListWeatherFragment.context)
            adapter = groupAdapter
        }

        groupAdapter.setOnItemClickListener { item, view ->
            (item as FutureWeatherItem).let {
                showWeatherDetail(it.weatherEntry.date, view)
            }
        }
    }

    private fun showWeatherDetail(date: LocalDate, view: View) {
        val dateString = LocalDateConverter.dateToString(date)!!
        val actionDetail = FutureListWeatherFragmentDirections.actionDetail(dateString)
        Navigation.findNavController(view).navigate(actionDetail)
    }
}