import BottomNavbar from '../BottomNavbar';

export default function Main() {
  // const { count, increment, decrement } = useCounter();

  return (
    <div className="flex min-h-screen flex-col items-center justify-between bg-orange-50 p-0">
      <div>찜한 가게들 리스트</div>

      <BottomNavbar />
    </div>
  );
}
