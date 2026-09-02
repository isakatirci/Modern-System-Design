package com.systemdesign.pastebin.domain;

/**
 * Paste erişim seviyesini tanımlayan enum — visibility modeli.
 * <p>
 * System design kavramı: <b>access control / visibility tiers</b> — paste'in
 * kimler tarafından keşfedilebileceğini sınıflandırır (public listing, unlisted link,
 * private). Bu demo'da API seviyesinde temel filtreleme için metadata olarak saklanır.
 * <p>
 * {@link Paste} entity'sinde persist edilir; {@link com.systemdesign.pastebin.web.dto.CreatePasteRequest}
 * ve {@link com.systemdesign.pastebin.web.dto.PasteResponse} ile API sınırında taşınır.
 */
public enum PasteVisibility {
    /** Herkes tarafından listelenebilir / erişilebilir. */
    PUBLIC,
    /** Sadece link bilen erişebilir; public listing'de görünmez. */
    UNLISTED,
    /** Yalnızca yetkili kullanıcılar (ileride auth ile) erişebilir. */
    PRIVATE
}
