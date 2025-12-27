## Helm install
This helm script installs the WebShell within a kubernetes cluster.

```bash
helm install webshell . -n shells --create-namespace
```

```bash
kubectl get secret webshell-tls -n istio-ingress
kubectl get gateway,virtualservice -n shells
```

```bash
helm upgrade webshell . -n shells 
```

```bash
helm uninstall webshell -n shells
```
