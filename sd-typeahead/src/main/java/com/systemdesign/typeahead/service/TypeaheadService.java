package com.systemdesign.typeahead.service;

import com.systemdesign.typeahead.trie.PrefixTrie;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Typeahead iş mantığı katmanı; kelime indeksleme ve öneri sorgularını yönetir.
 * <p>
 * In-memory {@code PrefixTrie} tutar; production ortamında bu yapı genelde
 * dağıtık cache veya search engine ile değiştirilir.
 */
@Service
public class TypeaheadService {

    private final PrefixTrie trie = new PrefixTrie();

    /**
     * Yeni bir kelime veya cümleyi trie'ye ekler.
     *
     * @param phrase    indekslenecek metin
     * @param frequency popülerlik skoru; öneri sıralamasında kullanılır
     */
    public void index(String phrase, int frequency) {
        trie.insert(phrase, frequency);
    }

    /**
     * Prefix ile başlayan kelimeleri popülerlik sırasına göre döner.
     *
     * @param prefix kullanıcının yazdığı prefix
     * @param limit  maksimum sonuç sayısı
     * @return öneri listesi
     */
    public List<String> suggest(String prefix, int limit) {
        return trie.suggest(prefix, limit);
    }
}
