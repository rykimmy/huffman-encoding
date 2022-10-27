# Huffman Encoding
Utilizes Huffman Encoding to compress and decompress files without loss.

## Overview
This program reads through a given file and uses Huffman Encoding to efficiently create codes for characters that are systematically arranged in a tree. Characters are mapped to their frequencies and put through a priority queue in order to properly order themselves in a tree that provides codes for each character through its traversal. These codes allow the file to be compressed and offer the path to decompression.

Files can be compressed with or without loss, and Huffman's scheme allows for a lossless form of compression as opposed to ASCII compression.

## Execution
To start the program, open and run HuffmanTree.java's main program. This will prompt a compression and decompression of Tolstoy's War and Peace.
