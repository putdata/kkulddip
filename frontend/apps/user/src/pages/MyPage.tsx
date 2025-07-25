import BottomNavbar from '../BottomNavbar';

export default function Main() {
  // const { count, increment, decrement } = useCounter();

  return (
    <div className="flex min-h-screen flex-col items-center justify-between bg-orange-50 p-0">
      <div>메인 화면</div>

      <BottomNavbar />
    </div>
  );
}
