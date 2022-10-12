package no.oslomet.cs.algdat.Oblig2;


////////////////// class DobbeltLenketListe //////////////////////////////


import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;


public class DobbeltLenketListe<T> implements Liste<T> {

    /**
     * Node class
     *
     * @param <T>
     */
    private static final class Node<T> {
        private T verdi;                   // nodens verdi
        private Node<T> forrige, neste;    // pekere

        private Node(T verdi, Node<T> forrige, Node<T> neste) {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        private Node(T verdi) {
            this(verdi, null, null);
        }
    }

    // instansvariabler
    private Node<T> hode;          // peker til den første i listen
    private Node<T> hale;          // peker til den siste i listen
    private int antall;            // antall noder i listen
    private int endringer;         // antall endringer i listen

    public DobbeltLenketListe() {
        hode = null;
        hale = null;
        antall = 0;
        endringer = 0;
    }

    public DobbeltLenketListe(T[] a) {
        this.hode = null;
        this.hale = null;

        //her sjekker jeg om tabellen er tøm, og hvis ja sender feil mld
        if (a == null) {
            throw new NullPointerException("tabbelen a er null!");
        }
        if(a.length > 0){
            int i = 0;
            for (; i<a.length; i++){
                if (a[i] != null) {
                    hode = new Node<>(a[i]);
                    antall++;
                    break;
                }
            }
            hale = hode;
            if (hale != null) {
                i++;
                for(; i<a.length; i++){
                    if (a[i] != null) {
                        hale.neste = new Node<>(a[i], hale, null);
                        hale = hode.neste;
                        antall++;
                    }
                }
            }
        }
    }

    public Liste<T> subliste(int fra, int til) {
        fratilkontroll(antall, fra, til);

        Liste<T> liste = new DobbeltLenketListe<>();
        int lengde = til- fra;

        if(lengde < 1){
            return liste;
        }

        Node<T> current  = finnNode(fra);

        while (lengde>0){
            liste.leggInn(current.verdi);
            current = current.neste;
            lengde--;
        }
        return liste;
    }

    //vi bruekr her en hjelpemetode
    private void fratilkontroll(int antall, int fra, int til) {
        if (fra < 0) {                                // fra er negativ
            throw new IndexOutOfBoundsException("fra(" + fra + ") er negativ!");
        }
        if (til > antall) {                        // til er utenfor tabellen
            throw new IndexOutOfBoundsException("til(" + til + ") > antall(" + antall + ")");
        }
        if (fra > til) {                               // fra er større enn til
            throw new IllegalArgumentException("fra(" + fra + ") > til(" + til + ") - illegalt intervall!");
        }
    }

    @Override
    public int antall() {
        return antall;
    }

    @Override
    public boolean tom() {
        if(antall > 0){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean leggInn(T verdi) {
        verdi = Objects.requireNonNull(verdi, "ikke tillat med null");

        Node<T> newnode = new Node<>(verdi);

        if (antall == 0 && hale == null && hode == null) {
            hode = newnode;
            hale = hode;
        } else {
            newnode.forrige = hale;
            hale.neste = newnode;
            newnode.forrige = hale;
            hale = newnode;
        }
        endringer++;
        antall++;
        return true;
    }

    @Override
    public void leggInn(int indeks, T verdi) {
        Objects.requireNonNull(verdi,"Ikke tillat med null");
        //1- sjekk for negativ og indeks mindre enn antall
        if(indeks < 0 || indeks > antall){
            verdi = Objects.requireNonNull(verdi,"indeks mindre enn null, negativ og større enn antall er ikke lov");
        }
       // indeksKontroll(indeks, true);

        //tom liste
        if(antall ==  0 && indeks == 0){
            hode = hale = new Node<T>(verdi, null, null);
        }else if(indeks == 0){
            Node<T> newNode = new Node<T>(verdi,null,hode);
            hode.neste.forrige = hode;

        }else if(indeks == antall) {
            hale = new Node<T>(verdi, hale, null);
            hale.forrige.neste = hale;
        }else {
            Node<T> node = hode;
            for(int i=0; i<indeks; i++){
                node = node.neste;
                node = new Node<T>(verdi,node.forrige,node);
                node.neste.forrige = node.forrige.neste=node;
            }
            antall++;
            endringer++;
        }
    }

    @Override
    public boolean inneholder(T verdi) {
        //koppiert en del kode fra kompednie(program kode 3,2,2 g)
        if (indeksTil(verdi) == -1){
            return false;
        }else {
            return true;
        }

    }

    @Override
    public T hent(int indeks) {// kopiert fra kampendia (programkode 3,3,3 b)
        indeksKontroll(indeks,false);
        return finnNode(indeks).verdi;
    }

    private Node<T> finnNode(int indeks) {
        indeksKontroll(indeks,false);


        Node<T> current;
            if (indeks <= antall / 2) {
                current = hode;
               for(int i=0; i<indeks; i++){
                   current = current.neste;
                }
            }else{
                    current = hale;
                    for(int i=antall-1; i>indeks; i--){
                        current =current.neste;
                    }
            }
        return current;
        }


    @Override
    public int indeksTil(T verdi) {//har fått tips via denne netsiden (https://www.cs.hioa.no/~ulfu/appolonius/kildekode/TabellListe.html)
        if (antall <= 0 || verdi == null) {
            return -1;
        }
        Node<T> p = hode;
        for (int i = 0; i < antall; i++) {
            if (p.verdi.equals(verdi)) {
                return i;
            }
            p = p.neste;
        }
        return -1;
    }

    @Override
    public T oppdater(int indeks, T nyverdi) {
        nyverdi = Objects.requireNonNull(nyverdi, "Ikke tillatt med null-verdier!");
        indeksKontroll(indeks, false);  // Se Liste, false: indeks = antall er ulovlig

        Node<T> newNode = finnNode(indeks);
        T gammelVerdi = newNode.verdi;
        newNode.verdi = nyverdi;
        endringer ++;
        return gammelVerdi;
    }

    @Override
    public boolean fjern(T verdi) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T fjern(int indeks) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void nullstill() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        StringBuilder liste = new StringBuilder();
        liste.append("[");
        Node<T> current =hode;
       /* while(current == null) {
            liste.append("]");
            System.out.println(liste);
            break;
        }

        */
        while(current != null) {
            if (current == hale) {
                liste.append(current.verdi);
            } else {
                liste.append(current.verdi);
                liste.append(", ");
            }
            current = current.neste;
        }
        liste.append("]");
        return liste.toString();
    }

    public String omvendtString() {
        StringBuilder liste = new StringBuilder();
        liste.append("[");
        Node<T> current = hale;
        /*
        while (current == null || antall == 0){
            System.out.println(liste.append("]"));
            break;
        }

         */
        while(current != null) {
            if (current == hode) {
                liste.append(current.verdi);
            } else {
                liste.append(current.verdi);
                liste.append(", ");
            }
            current = current.forrige;
        }
        liste.append("]");
        return liste.toString();
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    public Iterator<T> iterator(int indeks) {
        throw new UnsupportedOperationException();
    }

    private class DobbeltLenketListeIterator implements Iterator<T> {
        private Node<T> denne;
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator() {
            denne = hode;     // p starter på den første i listen
            fjernOK = false;  // blir sann når next() kalles
            iteratorendringer = endringer;  // teller endringer
        }

        private DobbeltLenketListeIterator(int indeks) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return denne != null;
        }

        @Override
        public T next() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    } // class DobbeltLenketListeIterator

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c) {
        throw new UnsupportedOperationException();
    }

} // class DobbeltLenketListe


