package com.example.app_sudamericana.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.app_sudamericana.API.Domain.Response.ReservationResponse
import com.example.app_sudamericana.API.Service.ReservationService
import com.example.app_sudamericana.EditarPerfilActivity
import com.example.app_sudamericana.R
import com.example.app_sudamericana.enviroments.Credentials
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers


class PerfilFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var spInstance: SharedPreferences;
    private lateinit var nameProfile: TextView
    private lateinit var emailProfile: TextView
    var linearLayaoutEditPerfil: LinearLayout? = null
    val disposables: CompositeDisposable = CompositeDisposable()


    var reservationService: ReservationService = ReservationService();
    override fun onCreate(savedInstanceState: Bundle?) {
        this.spInstance = requireActivity().getSharedPreferences(
            Credentials.NAME_PREFERENCES,
            Context.MODE_PRIVATE
        );

        super.onCreate(savedInstanceState)

        val token = this.spInstance.getString(Credentials.TOKEN_JWT, "");
        if (token != null) {
            reservationService.getAllReservations(token).subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ReservationResponse> {
                    @Override
                    override fun onSubscribe(d: Disposable) {
                        disposables.add(d)
                    }

                    @Override
                    override fun onError(e: Throwable) {
                        if (e.message.toString().equals("HTTP 403 Forbidden")) {
                            Toast.makeText(
                                context,
                                "BORRAR LA SESSION Y VOLVER A INICIAR",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    @Override
                    override fun onComplete() {
                    disposables.clear()
                    }

                    override fun onNext(t: ReservationResponse?) {
                        Toast.makeText(context, "hay respuesta", Toast.LENGTH_SHORT).show()

                    }
                })
        } else {
            Toast.makeText(context, "token vacio", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayaoutEditPerfil = view.findViewById(R.id.linearLayaoutEditPerfil)

        linearLayaoutEditPerfil?.setOnClickListener {
            context?.let { safeContext ->
                val intent = Intent(safeContext, EditarPerfilActivity::class.java)
                startActivity(intent)
            }
        }

        // setear datos a los textview
        nameProfile = view.findViewById(R.id.textViewUsername);
        emailProfile = view.findViewById(R.id.textViewEmail);

        val emailText = this.spInstance.getString(Credentials.USER_EMAIL, "email vacio");
        val nameText = this.spInstance.getString(Credentials.USER_FIRSTNAME, "nombre vacio");

        nameProfile.setText(nameText);
        emailProfile.setText(emailText);
    }



}