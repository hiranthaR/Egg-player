package xyz.hirantha.jajoplayer.internal

interface Playable {
    fun onTrackPrevious()
    fun onTrackPlay()
    fun onTrackPause()
    fun onTrackNext()
}