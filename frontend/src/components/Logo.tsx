import { Link } from 'react-router-dom';

export function Logo() {
  return <Link className="brand" to="/"><span className="brand-mark">H</span><span>Hola<span className="brand-accent">.</span></span></Link>;
}
