package com.example.trackr.ui;

/**
 * @author Rameel Hassan
 * Created 13/06/2023 at 5:33 PM
 */
import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

public class DataBindingsUtils {



//    private MyBinding binding;
//    binding = DataBindingsUtils.dataBindings(this, MyBinding::inflate).getValue();


    public static <BindingT extends ViewDataBinding> Lazy<BindingT> dataBindings(Fragment fragment, final BindingCreator<BindingT> bindingCreator) {
        return new Lazy<BindingT>() {
            private BindingT cached;
            private LifecycleEventObserver observer = (source, event) -> {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    cached = null;
                }
            };
            @Override
            public BindingT getValue() {
                if (cached == null) {
                    BindingT binding = bindingCreator.createBinding(fragment.requireView());
                    binding.setLifecycleOwner(fragment.getViewLifecycleOwner());
                    fragment.getViewLifecycleOwner().getLifecycle().addObserver(observer);
                    cached = binding;
                }
                return cached;
            }

            @Override
            public boolean isInitialized() {
                return cached != null;
            }
        };
    }

    public interface BindingCreator<BindingT extends ViewDataBinding> {
        BindingT createBinding(View view);
    }

    public interface Lazy<T> {
        T getValue();
        boolean isInitialized();
    }
}

