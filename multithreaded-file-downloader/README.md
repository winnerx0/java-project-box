# Multithreaded File Downloader

A simple multithreaded file downloader written in Java using the built-in `HttpClient`. It downloads large files in parallel using multiple threads, significantly speeding up the download process.

---

## Features

* Downloads files from a given URL using multiple threads.
* Uses HTTP Range headers to request file chunks.
* Automatically infers filename from Content-Disposition or URL.
* Efficient writing using `RandomAccessFile`.
* Graceful shutdown and await termination.

---

## Requirements

* Java 11 or above (for `HttpClient` API).

---

## How it Works

1. Sends a HEAD request to determine file size and filename.
2. Splits the file into equal chunks based on the number of threads.
3. Each thread downloads a chunk using HTTP Range requests.
4. Threads write their chunks to the correct position in a shared file using `RandomAccessFile`.

---

## Usage

### Compile:

```bash
javac MultithreadedFileDownloader.java
```

### Run:

```bash
java MultithreadedFileDownloader <file_url>
```

Example:

```bash
java MultithreadedFileDownloader https://example.com/largefile.zip
```

---

## Notes

* The server must support `Range` requests (responds with HTTP 206 Partial Content).
* Uses 10 threads by default. You can adjust `numThreads` in the source code.
* The downloaded file is saved in the current working directory.

---

## Example Output

```
----Starting Multithreaded File Downloader----
Downloading from 0mb to 9 mb
Downloading from 9mb to 18 mb
...
Download complete from ...
Download complete took 15 seconds
```

---

## License

MIT License. Feel free to use and modify for personal or commercial projects.
