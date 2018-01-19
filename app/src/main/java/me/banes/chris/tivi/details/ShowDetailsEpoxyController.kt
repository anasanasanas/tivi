/*
 * Copyright 2018 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.banes.chris.tivi.details

import android.content.Context
import android.view.View
import com.airbnb.epoxy.EpoxyController
import me.banes.chris.tivi.R
import me.banes.chris.tivi.data.entities.TiviShow
import me.banes.chris.tivi.detailsBadge
import me.banes.chris.tivi.detailsSummary
import me.banes.chris.tivi.detailsTitle
import me.banes.chris.tivi.header
import me.banes.chris.tivi.posterGridItem
import me.banes.chris.tivi.tmdb.TmdbImageUrlProvider
import me.banes.chris.tivi.ui.epoxy.TotalSpanOverride

class ShowDetailsEpoxyController(
        private val context: Context,
        private val callbacks: Callbacks
) : EpoxyController() {

    interface Callbacks {
        fun onItemClicked(item: TiviShow)
    }

    var show : TiviShow? = null
        set(value) {
            if (value != field) {
                field = value
                requestModelBuild()
            }
        }

    var imageProvider : TmdbImageUrlProvider? = null
        set(value) {
            if (value != field) {
                field = value
                requestModelBuild()
            }
        }

    var relatedShows : List<TiviShow>? = null
        set(value) {
            if (value != field) {
                field = value
                requestModelBuild()
            }
        }


    override fun buildModels() {
        detailsTitle {
            id("title")
            title(show?.title)
            subtitle(show?.originalTitle)
            genres(show?.genres)
            spanSizeOverride(TotalSpanOverride)
        }

        show?.rating?.let { rating ->
            detailsBadge {
                val ratingOutOfOneHundred = Math.round(rating * 10)
                id("rating")
                label(context.getString(R.string.percentage_format, ratingOutOfOneHundred))
                icon(R.drawable.ic_details_rating)
                contentDescription(context.getString(R.string.rating_content_description_format, ratingOutOfOneHundred))
            }
        }
        show?.network?.let { network ->
            detailsBadge {
                id("network")
                label(network)
                icon(R.drawable.ic_details_network)
                contentDescription(context.getString(R.string.network_content_description_format, network))
            }
        }
        show?.certification?.let { certificate ->
            detailsBadge {
                id("cert")
                label(certificate)
                icon(R.drawable.ic_details_certificate)
                contentDescription(context.getString(R.string.certificate_content_description_format, certificate))
            }
        }
        show?.runtime?.let { runtime ->
            detailsBadge {
                val runtimeMinutes = context.getString(R.string.minutes_format, runtime)
                id("runtime")
                label(runtimeMinutes)
                icon(R.drawable.ic_details_runtime)
                contentDescription(context.resources?.getQuantityString(R.plurals.runtime_content_description_format, runtime, runtime))
            }
        }

        detailsSummary {
            id("summary")
            summary(show?.summary)
            spanSizeOverride(TotalSpanOverride)
        }

        if (relatedShows != null) {
            header {
                id("related_header")
                title(R.string.related_title)
                spanSizeOverride(TotalSpanOverride)
            }

            for (show in relatedShows!!) {
                posterGridItem {
                    id(show.traktId!!.toLong())
                    title(show.title)
                    tmdbImageUrlProvider(imageProvider)
                    posterPath(show.tmdbPosterPath)
                    clickListener(View.OnClickListener {
                        callbacks.onItemClicked(show)
                    })
                }
            }
        }
    }
}