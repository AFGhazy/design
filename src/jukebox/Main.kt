package jukebox

fun main() {
	val playlist = Playlist("romance", listOf(Song("A"), Song("B"), Song("C"), Song("D")))
	val pl2 = Playlist("anime", listOf(Song("AA"), Song("BB"), Song("CC")))
	
	
	playlist.add(Song("E"))
	playlist.delete(Song("A"))
	playlist.shuffle()
	println(playlist)
	
	val jukeBox = Jukebox(CDPlayer(playlist))
	jukeBox.playSong()
	jukeBox.playSong()
	jukeBox.playSong()
	jukeBox.playSong()
	
	jukeBox.playSong()
	jukeBox.playSong()
	jukeBox.playSong()
	jukeBox.playSong()
	
	jukeBox.playSong()
	jukeBox.playSong()
	jukeBox.playSong()
	jukeBox.playSong()
	
	jukeBox.cdPlayer.playlist = pl2
	
	jukeBox.playSong()
	jukeBox.playSong()
	jukeBox.playSong()
	jukeBox.playSong()
	
	println(jukeBox)
}


data class Jukebox(val cdPlayer: CDPlayer) {
	fun playSong() {
		cdPlayer.playCurSong()
	}
}

class CDPlayer(playlist: Playlist) {
	
	private val display = Display("D")
	var playlist: Playlist = playlist
		set(value) {
			curSongIdx = 0
			field = value
		}
	
	private var curSongIdx = 0
	
	private fun getCurSong(): Song = playlist.songs[curSongIdx].also {
		curSongIdx += 1
		curSongIdx %= playlist.songs.size
	}
	
	fun playCurSong() {
		display.show(getCurSong())
	}
	
	override fun toString() = playlist.toString()
}

class Playlist(val name: String, songs: List<Song>) {
	private val _songs: MutableList<Song> = songs.toMutableList()
	
	
	val songs: List<Song>
		get() = _songs
	
	fun add(song: Song) {
		_songs.add(song)
	}
	
	fun delete(song: Song) {
		_songs.remove(song)
	}
	
	fun shuffle() {
		_songs.shuffle()
	}
	
	override fun toString() = "$name: ${_songs.toString()}"
}

data class Song(val name: String, val artist: Artist = Artist("Michael Jackson"), val album: Album = Album("Al"))

data class Artist(val name: String)

data class Album(val name: String)

data class Display(val id: String) {
	fun show(song: Song) {
		println(song)
	}
}