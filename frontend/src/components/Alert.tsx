export function Alert({ tone = 'error', children }: { tone?: 'error' | 'success' | 'info'; children: React.ReactNode }) {
  return <div className={`alert alert-${tone}`} role="alert">{children}</div>;
}
