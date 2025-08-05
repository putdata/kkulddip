// pages/PaymentRouteWrapper.tsx
import Payment from './Payment';
import { useNavigate } from 'react-router-dom';

export default function PaymentRouteWrapper() {
  const navigate = useNavigate();

  return (
    <Payment onBack={() => navigate(-1)} onComplete={() => navigate('/')} />
  );
}
