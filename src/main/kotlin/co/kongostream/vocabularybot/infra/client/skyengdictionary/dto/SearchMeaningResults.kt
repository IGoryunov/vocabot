package co.kongostream.vocabularybot.infra.client.skyengdictionary.dto

class SearchMeaningResults : ArrayList<Results>()

class Results(val meanings: ArrayList<Meaning>)