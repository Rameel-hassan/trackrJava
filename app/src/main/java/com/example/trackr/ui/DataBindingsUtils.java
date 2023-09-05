package com.example.trackr.ui;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

public class DataBindingsUtils {

    public static <BindingT extends ViewDataBinding> BindingT createBinding(Fragment fragment, BindingCreator<BindingT> bindingCreator) {
        BindingT binding = bindingCreator.createBinding(fragment.requireView());
        binding.setLifecycleOwner(fragment.getViewLifecycleOwner());
        fragment.getViewLifecycleOwner().getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    binding.unbind();
                }
            }
        });
        return binding;
    }

    public interface BindingCreator<BindingT extends ViewDataBinding> {
        BindingT createBinding(View view);
    }
}
