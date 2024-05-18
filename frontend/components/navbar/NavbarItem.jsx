import Link from "next/link";
const NavbarItem = ({ name, route, isActive, icon: Icon }) => {
  const baseItemClasses =
    "flex items-center transition duration-100 ease-in-out scale-90 hover:scale-100 rounded py-1";

  const activeClasses = isActive
    ? `bg-[#50599C] text-white font-bold ${baseItemClasses}`  // Botón morado con texto y SVG en blanco
    : `bg-white text-black ${baseItemClasses} hover:bg-gray-100`;

  const itemClasses = `${baseItemClasses} ${activeClasses}`;

  return (
    <li className={itemClasses}>
      <Link href={route} className="w-full h-full">
        <href className="flex items-center px-3 rounded-2xl w-full h-full ">
          {Icon && <Icon className="mr-2" fill="currentColor" width={25} height={25} />}
          <span className="text-lg font-sans ml-2">{name}</span>
        </href>
      </Link>
    </li>
  );
};

export default NavbarItem;
