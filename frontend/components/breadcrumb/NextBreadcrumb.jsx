"use client"
import React from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { MdArrowBack } from "react-icons/md";

const NextBreadcrumb = ({
  homeElement,
  separator,
  containerClasses,
  listClasses,
  activeClasses,
  capitalizeLinks,
}) => {
  const router = useRouter();
  const paths = usePathname();
  const pathNames = paths.split("/").filter((path) => path);

  return (
    <div className="px-10">
      <ul className={containerClasses}>
        <li className={listClasses}>
          <Link href={"/"}>{homeElement}</Link>
        </li>
        {pathNames.length > 0 && separator}
        {pathNames.map((link, index) => {
          let href = `/${pathNames.slice(0, index + 1).join("/")}`;
          let itemClasses = paths.includes(href)
            ? `${listClasses} ${activeClasses}`
            : listClasses;
          let itemLink = capitalizeLinks
            ? link[0].toUpperCase() + link.slice(1)
            : link;

          return (
            <React.Fragment key={index}>
              <li className={itemClasses}>
                <Link href={href}>{itemLink}</Link>
              </li>
              {pathNames.length !== index + 1 && separator}
            </React.Fragment>
          );
        })}
      </ul>
      {paths !== "/" && (
        <div>
          <button
            className={activeClasses}
            type="button"
            onClick={() => router.back()}
          >
            <div className="flex items-center">
              <MdArrowBack className="ml-4 mr-2" />
              <span>Volver</span>
            </div>
          </button>
        </div>
      )}
    </div>
  );
};

export default NextBreadcrumb;
